package project.camus.hexagonal.web.controller.task;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import project.camus.common.SuccessResponse;
import project.camus.hexagonal.web.controller.task.delegator.TaskFindAllDelegator;
import project.camus.hexagonal.web.controller.task.response.FindAllTasksResponse;
import project.camus.webmvc.common.ResponseWrapper;

@RestController
@RequiredArgsConstructor
public class TaskFindAllController extends V1TaskController {

    private final TaskFindAllDelegator taskFindAllDelegator;

    @GetMapping()
    @PageableAsQueryParam
    public ResponseEntity<SuccessResponse<FindAllTasksResponse>> findAllTasks(
        @ParameterObject @PageableDefault(sort = {"priority"}, direction = Direction.DESC) Pageable pageable) {

        return ResponseWrapper.ok(taskFindAllDelegator.findAllTasks(pageable));
    }
}
