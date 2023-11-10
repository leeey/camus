package project.camus.hexagonal.usecase.task;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import project.camus.hexagonal.port.task.TaskPort;
import project.camus.hexagonal.usecase.task.dto.request.CreateTaskRequestUseCaseDto;
import project.camus.hexagonal.usecase.task.dto.response.CreateTaskResponseUseCaseDto;
import project.camus.hexagonal.usecase.task.dto.response.FindAllTasksResponseUseCaseDto;
import project.camus.hexagonal.usecase.task.mapper.TaskUseCaseMapper;

@Component
@RequiredArgsConstructor
public class TaskUseCase {

    private static final TaskUseCaseMapper MAPPER = TaskUseCaseMapper.INSTANCE;

    private final TaskPort taskPort;

    public CreateTaskResponseUseCaseDto createTask(CreateTaskRequestUseCaseDto dto) {

        return taskPort.createTask(MAPPER.toPortDto(dto.getTask()));
    }

    public FindAllTasksResponseUseCaseDto findAllTasks(Pageable pageable) {

        return taskPort.findAllTasks(pageable);
    }

    public void deleteTaskById(Long id) {

        taskPort.deleteTaskById(id);
    }
}
