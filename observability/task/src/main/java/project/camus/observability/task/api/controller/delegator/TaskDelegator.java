package project.camus.observability.task.api.controller.delegator;

import io.micrometer.observation.annotation.Observed;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.camus.observability.task.api.service.TaskDummyService;
import project.camus.observability.task.domain.dto.TaskDto;

@Component
@RequiredArgsConstructor
public class TaskDelegator {

    private final TaskDummyService taskDummyService;

    @Observed(name = "TaskDelegator", contextualName = "findTasksByMemberId")
    public List<TaskDto> findTasksByMemberId(Long id) {

        return taskDummyService.findTasksByMemberId(id);
    }
}
