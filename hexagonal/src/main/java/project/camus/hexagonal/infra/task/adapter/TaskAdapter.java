package project.camus.hexagonal.infra.task.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import project.camus.database.jpa.model.task.TaskDao;
import project.camus.database.jpa.model.task.TaskEntity;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskAdapter {

    private final TaskDao taskDao;

    public TaskEntity createTask(TaskEntity entity) {

        return taskDao.save(entity);
    }

    public Page<TaskEntity> findAllTasks(Pageable pageable) {

        return taskDao.findAllByPage(pageable);
    }

    public void delete(TaskEntity entity) {

        taskDao.delete(entity);
    }

    public TaskEntity findTaskById(Long id) {

        return taskDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("id"));
    }

    public TaskEntity updateTask(TaskEntity entity) {

        return taskDao.save(entity);
    }
}
