package project.camus.hexagonal.port.task.dto;

import java.math.BigInteger;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TaskPortDto {

    private BigInteger id;

    private String title;

    private String content;

    private Integer priority;

    private boolean archived;
}
