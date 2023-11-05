package project.camus.hexagonal.usecase.task.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateTaskResponseUseCaseDto {

    private Long id;

    private LocalDateTime createdAt;
}
