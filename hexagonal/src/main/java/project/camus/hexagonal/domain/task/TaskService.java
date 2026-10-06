package project.camus.hexagonal.domain.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.camus.database.jpa.model.task.TaskEntity;
import project.camus.hexagonal.domain.task.event.TaskDomainEvent;
import project.camus.hexagonal.domain.task.event.TaskEventPort;
import project.camus.hexagonal.domain.task.mapper.TaskServiceMapper;
import project.camus.hexagonal.infra.task.adapter.TaskAdapter;
import project.camus.hexagonal.port.task.dto.TaskPortDto;
import project.camus.hexagonal.port.task.dto.response.FindAllTasksResponsePortDto;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private static final TaskServiceMapper MAPPER = TaskServiceMapper.INSTANCE;

    private final TaskAdapter taskAdapter;

    private final TaskEventPort taskEventPort;

    public TaskPortDto createTask(TaskEntity entity) {

        TaskEntity created = taskAdapter.createTask(entity);
        taskEventPort.append(TaskDomainEvent.of(TaskDomainEvent.Type.CREATED, created));
        log.info("task created. id={}", created.getId());
        return MAPPER.toPortDto(created);
    }

    @Transactional(readOnly = true)
    public FindAllTasksResponsePortDto findAllTasks(Pageable pageable) {

        return MAPPER.toPortDto(taskAdapter.findAllTasks(pageable));
    }

    public void deleteTaskById(Long id) {

        TaskEntity entity = findTaskById(id);
        taskAdapter.delete(entity);
        taskEventPort.append(TaskDomainEvent.of(TaskDomainEvent.Type.DELETED, entity));
        log.info("task deleted. id={}", id);
    }

    public TaskPortDto archiveTaskById(Long id) {

        TaskEntity entity = findTaskById(id);
        TaskEntity archived = taskAdapter.updateTask(entity.toBuilder().archived(true).build());
        taskEventPort.append(TaskDomainEvent.of(TaskDomainEvent.Type.ARCHIVED, archived));
        log.info("task archived. id={}", id);
        return MAPPER.toPortDto(archived);
    }

    private TaskEntity findTaskById(Long id) {

        return taskAdapter.findTaskById(id);
    }
}
