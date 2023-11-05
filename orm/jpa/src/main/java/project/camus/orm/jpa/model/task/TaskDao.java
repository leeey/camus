package project.camus.orm.jpa.model.task;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskDao {

    private final TaskRepository taskRepository;

    public Page<TaskEntity> finaAllTasks(Pageable pageable) {

        return taskRepository.findAll(pageable);
    }

    public List<TaskEntity> createTasks(List<TaskEntity> tasks) {

        return taskRepository.saveAll(tasks);
    }

    public TaskEntity createTask(TaskEntity task) {

        return taskRepository.save(task);
    }
}
