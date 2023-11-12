package project.camus.hexagonal.web.controller.task.delegator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.camus.hexagonal.usecase.task.TaskUseCase;
import project.camus.hexagonal.web.controller.task.response.ArchiveTaskResponse;

@Component
@RequiredArgsConstructor
public class TaskArchiveDelegator {

    private final TaskUseCase taskUseCase;

    public ArchiveTaskResponse archiveTaskById(Long id) {

        return taskUseCase.archiveTaskById(id);
    }
}
