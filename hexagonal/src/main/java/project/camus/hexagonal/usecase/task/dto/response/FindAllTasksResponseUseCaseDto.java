package project.camus.hexagonal.usecase.task.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.camus.hexagonal.usecase.task.dto.TaskUseCaseDto;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FindAllTasksResponseUseCaseDto {

    List<TaskUseCaseDto> tasks;
}
