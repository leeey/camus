package project.camus.hexagonal.web.controller.task.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.camus.hexagonal.common.enums.TaskPriorityType;

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
