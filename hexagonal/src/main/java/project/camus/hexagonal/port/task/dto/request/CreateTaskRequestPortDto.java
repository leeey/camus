package project.camus.hexagonal.port.task.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateTaskRequestPortDto {

    private String title;

    private String content;

    private Integer priority;

    private boolean archived;
}
