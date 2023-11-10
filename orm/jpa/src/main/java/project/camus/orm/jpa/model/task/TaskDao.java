package project.camus.orm.jpa.model.task;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class TaskDao {

    private final TaskRepository taskRepository;

    public Page<TaskEntity> findAllTasks(Pageable pageable) {

        return taskRepository.findAll(pageable);
    }

    public List<TaskEntity> createTasks(List<TaskEntity> tasks) {

        return taskRepository.saveAll(tasks);
    }

    public TaskEntity createTask(TaskEntity task) {

        return taskRepository.save(task);
    }

    public void deleteTaskById(Long id) {

        taskRepository.deleteById(id);
    }
}
