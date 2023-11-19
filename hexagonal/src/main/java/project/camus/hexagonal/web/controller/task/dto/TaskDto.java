package project.camus.hexagonal.web.controller.task.dto;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.camus.hexagonal.common.enums.TaskPriorityType;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    private BigInteger id;

    private String title;

    private String content;

    private TaskPriorityType priorityType;

    private boolean archived;
}
