package project.camus.hexagonal.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import project.camus.hexagonal.domain.service.mapper.TaskServiceMapper;
import project.camus.hexagonal.infra.jpa.task.adapter.JpaTaskAdapter;
import project.camus.hexagonal.port.task.dto.TaskPortDto;
import project.camus.hexagonal.port.task.dto.response.FindAllTasksResponsePortDto;
import project.camus.orm.jpa.model.task.TaskEntity;

@Service
@RequiredArgsConstructor
public class TaskService {

    private static final TaskServiceMapper MAPPER = TaskServiceMapper.INSTANCE;

    private final JpaTaskAdapter jpaTaskAdapter;

    public TaskPortDto createTask(TaskEntity entity) {

        return MAPPER.toPortDto(jpaTaskAdapter.createTask(entity));
    }

    public FindAllTasksResponsePortDto findAllTasks(Pageable pageable) {

        return MAPPER.toPortDto(jpaTaskAdapter.findAllTasks(pageable));
    }

    public void deleteTaskById(Long id) {

        TaskEntity entity = findTaskById(id);
        jpaTaskAdapter.delete(entity);
    }

    public TaskPortDto archiveTaskById(Long id) {

        TaskEntity entity = findTaskById(id);
        return MAPPER.toPortDto(jpaTaskAdapter.updateTask(entity.toBuilder().archived(true).build()));
    }

    private TaskEntity findTaskById(Long id) {

        return jpaTaskAdapter.findTaskById(id);
    }
}
