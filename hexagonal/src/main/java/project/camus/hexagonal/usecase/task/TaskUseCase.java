package project.camus.hexagonal.usecase.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import project.camus.hexagonal.port.task.TaskPort;
import project.camus.hexagonal.usecase.task.dto.request.CreateTaskRequestUseCaseDto;
import project.camus.hexagonal.usecase.task.mapper.TaskUseCaseMapper;
import project.camus.hexagonal.web.task.controller.response.ArchiveTaskResponse;
import project.camus.hexagonal.web.task.controller.response.CreateTaskResponse;
import project.camus.hexagonal.web.task.controller.response.FindAllTasksResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskUseCase {

    private static final TaskUseCaseMapper MAPPER = TaskUseCaseMapper.INSTANCE;

    private final TaskPort taskPort;

    public CreateTaskResponse createTask(CreateTaskRequestUseCaseDto dto) {

        return MAPPER.toCreateResponse(taskPort.createTask(MAPPER.toPortDto(dto.getTask())));
    }

    public FindAllTasksResponse findAllTasks(Pageable pageable) {

        return MAPPER.toFindAllResponse(taskPort.findAllTasks(pageable));
    }

    public void deleteTaskById(Long id) {

        taskPort.deleteTaskById(id);
    }

    public ArchiveTaskResponse archiveTaskById(Long id) {

        return MAPPER.toArchiveResponse(taskPort.archiveTaskById(id));
    }
}
