package project.camus.feign.task.api.controller.delegator;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.camus.feign.task.api.service.TaskDummyService;
import project.camus.feign.task.domain.dto.TaskDto;

@Component
@RequiredArgsConstructor
public class TaskDelegator {

    private final TaskDummyService taskDummyService;

    public List<TaskDto> findTasksByMemberId(Long id) {

        return taskDummyService.findTasksByMemberId(id);
    }
}
