package project.camus.hexagonal.usecase.task.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import project.camus.hexagonal.usecase.task.dto.TaskUseCaseDto;

@Builder
@Getter
public class FindAllTasksResponseUseCaseDto {

    List<TaskUseCaseDto> tasks;
}
