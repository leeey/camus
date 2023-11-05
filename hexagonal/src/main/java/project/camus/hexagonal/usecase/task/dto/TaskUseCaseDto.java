package project.camus.hexagonal.usecase.task.dto;

import java.math.BigInteger;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TaskUseCaseDto {

    private BigInteger id;

    private String title;

    private String content;

    private Integer priority;

    private boolean archived;
}
