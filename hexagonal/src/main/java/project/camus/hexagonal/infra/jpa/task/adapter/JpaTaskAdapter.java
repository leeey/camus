package project.camus.hexagonal.infra.jpa.task.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import project.camus.orm.jpa.model.task.TaskDao;
import project.camus.orm.jpa.model.task.TaskEntity;

@Component
@RequiredArgsConstructor
public class JpaTaskAdapter {

    private final TaskDao taskDao;

    public TaskEntity createTask(TaskEntity entity) {

        return taskDao.createTask(entity);
    }

    public Page<TaskEntity> findAllTasks(Pageable pageable) {

        return taskDao.findAllTasks(pageable);
    }

    public void deleteTaskById(Long id) {

        taskDao.deleteTaskById(id);
    }
}
