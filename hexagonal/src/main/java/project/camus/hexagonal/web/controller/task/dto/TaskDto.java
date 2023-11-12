package project.camus.hexagonal.web.controller.task.dto;

import java.math.BigInteger;
import lombok.Builder;
import lombok.Getter;
import project.camus.hexagonal.common.enums.TaskPriorityType;

@Builder
@Getter
public class TaskDto {

    private BigInteger id;

    private String title;

    private String content;

    private TaskPriorityType priorityType;

    private boolean archived;
}
