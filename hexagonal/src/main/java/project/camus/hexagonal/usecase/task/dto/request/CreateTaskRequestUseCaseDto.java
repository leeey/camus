package project.camus.hexagonal.usecase.task.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.camus.hexagonal.usecase.task.dto.TaskUseCaseDto;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTaskRequestUseCaseDto {

    TaskUseCaseDto task;
}
