package project.camus.hexagonal.web.task.delegator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.camus.hexagonal.usecase.task.TaskUseCase;
import project.camus.hexagonal.web.task.controller.request.CreateTaskRequest;
import project.camus.hexagonal.web.task.controller.response.CreateTaskResponse;
import project.camus.hexagonal.web.task.delegator.mapper.TaskWebMapper;

@Component
@RequiredArgsConstructor
public class TaskCreateDelegator {

    private static final TaskWebMapper MAPPER = TaskWebMapper.INSTANCE;

    private final TaskUseCase taskUseCase;

    public CreateTaskResponse createTask(CreateTaskRequest request) {

        return taskUseCase.createTask(MAPPER.toDto(request));
    }
}
