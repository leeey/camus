package project.camus.hexagonal.web.task.delegator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.camus.hexagonal.usecase.task.TaskUseCase;
import project.camus.hexagonal.web.task.delegator.mapper.TaskCreateMapper;
import project.camus.hexagonal.web.task.controller.request.CreateTaskRequest;
import project.camus.hexagonal.web.task.controller.response.CreateTaskResponse;

@Component
@RequiredArgsConstructor
public class TaskCreateDelegator {

    private static final TaskCreateMapper MAPPER = TaskCreateMapper.INSTANCE;

    private final TaskUseCase taskUseCase;

    public CreateTaskResponse createTask(CreateTaskRequest request) {

        return MAPPER.toResponse(taskUseCase.createTask(MAPPER.toDto(request)));
    }
}
