package project.camus.hexagonal.web.task.delegator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.camus.hexagonal.usecase.task.TaskUseCase;
import project.camus.hexagonal.web.task.controller.response.ArchiveTaskResponse;
import project.camus.hexagonal.web.task.delegator.mapper.TaskArchiveMapper;

@Component
@RequiredArgsConstructor
public class TaskArchiveDelegator {

    private static final TaskArchiveMapper MAPPER = TaskArchiveMapper.INSTANCE;

    private final TaskUseCase taskUseCase;

    public ArchiveTaskResponse archiveTaskById(Long id) {

        return MAPPER.toResponse(taskUseCase.archiveTaskById(id));
    }
}
