package project.camus.hexagonal.infra.task.outbox;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import project.camus.common.util.ObjectMapperUtil;
import project.camus.hexagonal.domain.task.event.TaskDomainEvent;
import project.camus.hexagonal.domain.task.event.TaskEventPort;

@Component
@RequiredArgsConstructor
public class TaskOutboxAdapter implements TaskEventPort {

    static final String AGGREGATE_TYPE = "TASK";

    static final String TRACE_PARENT = "traceparent";

    private final JdbcClient jdbcClient;

    private final ObjectProvider<Tracer> tracer;

    private final ObjectProvider<Propagator> propagator;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void append(TaskDomainEvent event) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("title", event.title());
        payload.put("content", event.content());
        payload.put("priority", event.priority());
        payload.put("archived", event.archived());

        jdbcClient.sql("""
                INSERT INTO outbox_event (event_id, aggregate_type, aggregate_id, event_type, payload, occurred_at, trace_parent)
                VALUES (:eventId, :aggregateType, :aggregateId, :eventType, CAST(:payload AS JSONB), :occurredAt, :traceParent)
                """)
            .param("eventId", event.eventId())
            .param("aggregateType", AGGREGATE_TYPE)
            .param("aggregateId", event.taskId())
            .param("eventType", event.type().name())
            .param("payload", ObjectMapperUtil.toJson(payload))
            .param("occurredAt", Timestamp.from(event.occurredAt()))
            .param("traceParent", currentTraceParent())
            .update();
    }

    /**
     * 현재 요청의 trace context 를 W3C traceparent 문자열로 꺼낸다. trace 가 없으면 null.
     */
    private String currentTraceParent() {

        Tracer currentTracer = tracer.getIfAvailable();
        Propagator currentPropagator = propagator.getIfAvailable();
        Span span = currentTracer == null ? null : currentTracer.currentSpan();
        if (span == null || currentPropagator == null) {
            return null;
        }
        Map<String, String> carrier = new HashMap<>();
        currentPropagator.inject(span.context(), carrier, Map::put);
        return carrier.get(TRACE_PARENT);
    }
}
