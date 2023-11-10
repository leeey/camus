package project.camus.hexagonal.web.task.delegator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.camus.hexagonal.usecase.task.TaskUseCase;

@Component
@RequiredArgsConstructor
public class TaskDeleteDelegator {

    private final TaskUseCase taskUseCase;

    public void deleteTaskById(Long id) {

        taskUseCase.deleteTaskById(id);
    }
}
