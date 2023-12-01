package project.camus.database.jpa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.camus.database.jpa.model.task.TaskDao;


@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskDao taskDao;

}
