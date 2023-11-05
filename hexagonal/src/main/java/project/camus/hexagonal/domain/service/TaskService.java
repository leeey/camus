package project.camus.hexagonal.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import project.camus.hexagonal.domain.service.mapper.TaskServiceMapper;
import project.camus.hexagonal.port.task.dto.response.CreateTaskResponsePortDto;
import project.camus.hexagonal.port.task.dto.response.FindAllTasksResponsePortDto;
import project.camus.orm.jpa.model.task.TaskDao;
import project.camus.orm.jpa.model.task.TaskEntity;

@Service
@RequiredArgsConstructor
public class TaskService {

    private static final TaskServiceMapper MAPPER = TaskServiceMapper.INSTANCE;

    private final TaskDao taskDao;

    public CreateTaskResponsePortDto createTask(TaskEntity entity) {

        return MAPPER.toPortDto(taskDao.createTask(entity));
    }

    public FindAllTasksResponsePortDto findAllTasks(Pageable pageable) {

        return MAPPER.toPortDto(taskDao.finaAllTasks(pageable));
    }
}
