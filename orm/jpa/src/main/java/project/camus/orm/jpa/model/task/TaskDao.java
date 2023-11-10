package project.camus.orm.jpa.model.task;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class TaskDao {

    private final TaskRepository taskRepository;

    public Page<TaskEntity> findAllByPage(Pageable pageable) {

        return taskRepository.findAll(pageable);
    }

    public List<TaskEntity> saveAll(List<TaskEntity> tasks) {

        return taskRepository.saveAll(tasks);
    }

    public TaskEntity save(TaskEntity task) {

        return taskRepository.save(task);
    }

    public void delete(TaskEntity entity) {

        taskRepository.delete(entity);
    }

    public Optional<TaskEntity> findById(Long id) {

        return taskRepository.findById(id);
    }
}
