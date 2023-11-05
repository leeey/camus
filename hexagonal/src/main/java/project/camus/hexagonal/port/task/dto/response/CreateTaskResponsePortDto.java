package project.camus.hexagonal.port.task.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateTaskResponsePortDto {

    private Long id;

    private LocalDateTime createdAt;
}
