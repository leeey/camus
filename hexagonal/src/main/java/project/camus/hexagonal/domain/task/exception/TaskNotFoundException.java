package project.camus.hexagonal.domain.task.exception;

import lombok.Getter;

@Getter
public class TaskNotFoundException extends RuntimeException {

    private final Long taskId;

    public TaskNotFoundException(Long taskId) {

        super("task not found. id=" + taskId);
        this.taskId = taskId;
    }
}
