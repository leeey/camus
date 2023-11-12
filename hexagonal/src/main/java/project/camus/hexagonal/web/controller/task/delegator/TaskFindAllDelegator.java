package project.camus.hexagonal.web.controller.task.delegator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import project.camus.hexagonal.usecase.task.TaskUseCase;
import project.camus.hexagonal.web.controller.task.response.FindAllTasksResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskFindAllDelegator {

    private final TaskUseCase taskUseCase;

    public FindAllTasksResponse findAllTasks(Pageable pageable) {

        return taskUseCase.findAllTasks(pageable);
    }
}
