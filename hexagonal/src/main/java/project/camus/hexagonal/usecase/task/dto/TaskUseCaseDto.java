package project.camus.hexagonal.usecase.task.dto;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskUseCaseDto {

    private BigInteger id;

    private String title;

    private String content;

    private Integer priority;

    private boolean archived;
}
