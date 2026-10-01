package project.camus.hexagonal.domain.task.event;

import java.time.Instant;
import java.util.UUID;
import project.camus.database.jpa.model.task.TaskEntity;

public record TaskDomainEvent(
    UUID eventId,
    Type type,
    Long taskId,
    String title,
    String content,
    Integer priority,
    boolean archived,
    Instant occurredAt
) {

    public enum Type {
        CREATED, ARCHIVED, DELETED
    }

    public static TaskDomainEvent of(Type type, TaskEntity task) {

        return new TaskDomainEvent(UUID.randomUUID(), type, task.getId(), task.getTitle(), task.getContent(),
            task.getPriority(), task.isArchived(), Instant.now());
    }
}
