package project.camus.hexagonal.web.task.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.camus.hexagonal.domain.enums.TaskPriorityType;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {

    @NotNull
    private String title;

    private String content;

    @NotNull
    private TaskPriorityType priorityType;
}
