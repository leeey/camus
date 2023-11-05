package project.camus.hexagonal.web.task.delegator;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import project.camus.hexagonal.usecase.task.TaskUseCase;
import project.camus.hexagonal.web.task.controller.response.FindAllTasksResponse;
import project.camus.hexagonal.web.task.delegator.mapper.TaskFindMapper;

@Component
@RequiredArgsConstructor
public class TaskFindAllDelegator {

    private static final TaskFindMapper MAPPER = TaskFindMapper.INSTANCE;

    private final TaskUseCase taskUseCase;

    public FindAllTasksResponse findAllTasks(Pageable pageable) {

        return MAPPER.toResponse(taskUseCase.findAllTasks(pageable));
    }
}
