package project.camus.hexagonal.port.task.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTaskRequestPortDto {

    private String title;

    private String content;

    private Integer priority;

    private boolean archived;
}
