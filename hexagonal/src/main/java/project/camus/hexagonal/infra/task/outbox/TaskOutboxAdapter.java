package project.camus.hexagonal.infra.task.outbox;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
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

    private final JdbcClient jdbcClient;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void append(TaskDomainEvent event) {

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("title", event.title());
        payload.put("content", event.content());
        payload.put("priority", event.priority());
        payload.put("archived", event.archived());

        jdbcClient.sql("""
                INSERT INTO outbox_event (event_id, aggregate_type, aggregate_id, event_type, payload, occurred_at)
                VALUES (:eventId, :aggregateType, :aggregateId, :eventType, CAST(:payload AS JSONB), :occurredAt)
                """)
            .param("eventId", event.eventId())
            .param("aggregateType", AGGREGATE_TYPE)
            .param("aggregateId", event.taskId())
            .param("eventType", event.type().name())
            .param("payload", ObjectMapperUtil.toJson(payload))
            .param("occurredAt", Timestamp.from(event.occurredAt()))
            .update();
    }
}
