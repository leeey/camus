package project.camus.hexagonal.domain.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import project.camus.hexagonal.domain.task.mapper.TaskServiceMapper;
import project.camus.hexagonal.infra.task.adapter.TaskAdapter;
import project.camus.hexagonal.port.task.dto.TaskPortDto;
import project.camus.hexagonal.port.task.dto.response.FindAllTasksResponsePortDto;
import project.camus.database.jpa.model.task.TaskEntity;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private static final TaskServiceMapper MAPPER = TaskServiceMapper.INSTANCE;

    private final TaskAdapter taskAdapter;

    public TaskPortDto createTask(TaskEntity entity) {

        return MAPPER.toPortDto(taskAdapter.createTask(entity));
    }

    public FindAllTasksResponsePortDto findAllTasks(Pageable pageable) {

        return MAPPER.toPortDto(taskAdapter.findAllTasks(pageable));
    }

    public void deleteTaskById(Long id) {

        TaskEntity entity = findTaskById(id);
        taskAdapter.delete(entity);
    }

    public TaskPortDto archiveTaskById(Long id) {

        TaskEntity entity = findTaskById(id);
        return MAPPER.toPortDto(taskAdapter.updateTask(entity.toBuilder().archived(true).build()));
    }

    private TaskEntity findTaskById(Long id) {

        return taskAdapter.findTaskById(id);
    }
}
