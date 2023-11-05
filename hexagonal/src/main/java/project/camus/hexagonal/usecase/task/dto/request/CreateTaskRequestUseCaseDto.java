package project.camus.hexagonal.usecase.task.dto.request;

import lombok.Builder;
import lombok.Getter;
import project.camus.hexagonal.usecase.task.dto.TaskUseCaseDto;

@Builder
@Getter
public class CreateTaskRequestUseCaseDto {

    TaskUseCaseDto task;
}
