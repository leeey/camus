package project.camus.kafka.consumer.usecase;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.camus.event.task.TaskEvent;
import project.camus.event.task.TaskEventType;

/**
 * task-events 로 task_summary 조회 테이블을 갱신한다.
 * <p>
 * 처리 이력(processed_event)과 조회 테이블을 같은 트랜잭션에서 기록하므로, 같은 이벤트가 다시 와도 한 번만 반영된다.
 */
@Slf4j
@Component
public class TaskEventUseCase {

    private final JdbcClient jdbcClient;

    private final Counter applied;

    private final Counter duplicated;

    public TaskEventUseCase(JdbcClient jdbcClient, MeterRegistry meterRegistry) {

        this.jdbcClient = jdbcClient;
        this.applied = Counter.builder("task.events.processed")
            .description("task events processed by the consumer")
            .tag("result", "applied")
            .register(meterRegistry);
        this.duplicated = Counter.builder("task.events.processed")
            .description("task events processed by the consumer")
            .tag("result", "duplicate")
            .register(meterRegistry);
    }

    /**
     * @return 처음 처리한 이벤트면 true, 이미 처리한 이벤트면 false
     */
    @Transactional
    public boolean process(TaskEvent event) {

        int inserted = jdbcClient.sql("""
                INSERT INTO processed_event (event_id, event_type)
                VALUES (:eventId, :eventType)
                ON CONFLICT (event_id) DO NOTHING
                """)
            .param("eventId", UUID.fromString(event.getEventId()))
            .param("eventType", event.getEventType().name())
            .update();
        if (inserted == 0) {
            log.info("skip duplicated task event. eventId={}", event.getEventId());
            duplicated.increment();
            return false;
        }

        // 늦게 도착한 이전 이벤트가 최신 상태를 덮어쓰지 않도록 last_event_at 을 비교한다.
        jdbcClient.sql("""
                INSERT INTO task_summary (task_id, title, content, priority, archived, deleted, last_event_at)
                VALUES (:taskId, :title, :content, :priority, :archived, :deleted, :occurredAt)
                ON CONFLICT (task_id) DO UPDATE
                SET title = EXCLUDED.title,
                    content = EXCLUDED.content,
                    priority = EXCLUDED.priority,
                    archived = EXCLUDED.archived,
                    deleted = EXCLUDED.deleted,
                    last_event_at = EXCLUDED.last_event_at,
                    updated_at = now()
                WHERE task_summary.last_event_at <= EXCLUDED.last_event_at
                """)
            .param("taskId", event.getTaskId())
            .param("title", event.getTitle())
            .param("content", event.getContent())
            .param("priority", event.getPriority())
            .param("archived", event.getArchived())
            .param("deleted", event.getEventType() == TaskEventType.DELETED)
            .param("occurredAt", Timestamp.from(event.getOccurredAt()))
            .update();
        applied.increment();
        return true;
    }
}
