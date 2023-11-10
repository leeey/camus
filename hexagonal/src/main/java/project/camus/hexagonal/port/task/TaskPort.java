package project.camus.hexagonal.port.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import project.camus.hexagonal.domain.service.TaskService;
import project.camus.hexagonal.port.task.dto.request.CreateTaskRequestPortDto;
import project.camus.hexagonal.port.task.mapper.TaskPortMapper;
import project.camus.hexagonal.usecase.task.dto.TaskUseCaseDto;
import project.camus.hexagonal.usecase.task.dto.response.FindAllTasksResponseUseCaseDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskPort {

    private static final TaskPortMapper MAPPER = TaskPortMapper.INSTANCE;

    private final TaskService taskService;

    public TaskUseCaseDto createTask(CreateTaskRequestPortDto portDto) {

        return MAPPER.toDto(taskService.createTask(MAPPER.toEntity(portDto)));
    }

    public FindAllTasksResponseUseCaseDto findAllTasks(Pageable pageable) {

        return MAPPER.toDto(taskService.findAllTasks(pageable));
    }

    public void deleteTaskById(Long id) {

        taskService.deleteTaskById(id);
    }

    public TaskUseCaseDto archiveTaskById(Long id) {

        return MAPPER.toDto(taskService.archiveTaskById(id));
    }
}
