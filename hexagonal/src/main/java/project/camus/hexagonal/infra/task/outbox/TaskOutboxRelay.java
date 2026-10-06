package project.camus.hexagonal.infra.task.outbox;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import project.camus.common.util.ObjectMapperUtil;
import project.camus.event.task.TaskEvent;
import project.camus.event.task.TaskEventType;
import project.camus.hexagonal.infra.kafka.KafkaTopicProperties;

/**
 * outbox_event 에 쌓인 이벤트를 kafka 로 발행한다.
 * <p>
 * postgresql advisory lock 으로 한 시점에 하나의 인스턴스만 발행하므로, 인스턴스가 여러 개여도 발행 순서(id 순)가 유지된다.
 * 발행 후 published_at 을 기록하기 전에 장애가 나면 같은 이벤트가 다시 발행될 수 있으므로(at-least-once), consumer 는 eventId 로 멱등 처리해야 한다.
 */
@Slf4j
@RequiredArgsConstructor
public class TaskOutboxRelay {

    static final long ADVISORY_LOCK_KEY = 7_070_001L;

    private final JdbcClient jdbcClient;

    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;

    private final OutboxProperties.Relay properties;

    private final KafkaTopicProperties.Topic topic;

    private final Tracer tracer;

    private final Propagator propagator;

    private final OutboxMetrics metrics;

    @Transactional
    @Scheduled(fixedDelayString = "${camus.outbox.relay.fixed-delay:1s}")
    public void relay() {

        Boolean locked = jdbcClient.sql("SELECT pg_try_advisory_xact_lock(:key)")
            .param("key", ADVISORY_LOCK_KEY)
            .query(Boolean.class)
            .single();
        if (!Boolean.TRUE.equals(locked)) {
            return;
        }

        List<OutboxEvent> events = jdbcClient.sql("""
                SELECT id, event_id, aggregate_id, event_type, payload::text AS payload, occurred_at, trace_parent
                FROM outbox_event
                WHERE published_at IS NULL
                ORDER BY id
                LIMIT :limit
                """)
            .param("limit", properties.batchSize())
            .query((rs, rowNum) -> new OutboxEvent(
                rs.getLong("id"),
                rs.getObject("event_id", UUID.class),
                rs.getLong("aggregate_id"),
                rs.getString("event_type"),
                rs.getString("payload"),
                rs.getTimestamp("occurred_at").toInstant(),
                rs.getString("trace_parent")))
            .list();

        for (OutboxEvent event : events) {
            if (!publish(event)) {
                // 순서 보장을 위해 실패한 이벤트 이후는 다음 주기에 다시 시도한다.
                break;
            }
            jdbcClient.sql("UPDATE outbox_event SET published_at = now() WHERE id = :id")
                .param("id", event.id())
                .update();
        }
    }

    /**
     * 이벤트를 기록한 요청의 trace 를 부모로 하는 span 안에서 발행한다.
     * kafka producer observation 이 이 span 을 이어받아 traceparent 헤더를 붙이므로, consumer 까지 하나의 trace 로 이어진다.
     */
    private boolean publish(OutboxEvent event) {

        Span span = startPublishSpan(event);
        try (Tracer.SpanInScope ignored = tracer.withSpan(span)) {
            kafkaTemplate.send(topic.name(), String.valueOf(event.aggregateId()), event.toAvro())
                .get(properties.sendTimeout().toMillis(), TimeUnit.MILLISECONDS);
            metrics.published();
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            span.error(e);
            metrics.failed();
            return false;
        } catch (Exception e) {
            log.warn("failed to publish outbox event. id={}, eventId={}", event.id(), event.eventId(), e);
            span.error(e);
            metrics.failed();
            return false;
        } finally {
            span.end();
        }
    }

    private Span startPublishSpan(OutboxEvent event) {

        Span.Builder builder = event.traceParent() == null
            ? tracer.spanBuilder()
            : propagator.extract(Map.of(TaskOutboxAdapter.TRACE_PARENT, event.traceParent()), Map::get);
        return builder.name("outbox publish")
            .tag("outbox.event.id", event.eventId().toString())
            .tag("outbox.event.type", event.eventType())
            .start();
    }

    record OutboxEvent(long id, UUID eventId, long aggregateId, String eventType, String payload, Instant occurredAt,
                       String traceParent) {

        TaskEvent toAvro() {

            Map<String, Object> fields = ObjectMapperUtil.readString(payload);
            return TaskEvent.newBuilder()
                .setEventId(eventId.toString())
                .setEventType(TaskEventType.valueOf(eventType))
                .setTaskId(aggregateId)
                .setTitle((String) fields.get("title"))
                .setContent((String) fields.get("content"))
                .setPriority((Integer) fields.get("priority"))
                .setArchived(Boolean.TRUE.equals(fields.get("archived")))
                .setOccurredAt(occurredAt)
                .build();
        }
    }
}
