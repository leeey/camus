package project.camus.hexagonal.web.controller.task;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.camus.common.SuccessResponse;
import project.camus.hexagonal.web.controller.task.delegator.TaskCreateDelegator;
import project.camus.hexagonal.web.controller.task.request.CreateTaskRequest;
import project.camus.hexagonal.web.controller.task.response.CreateTaskResponse;
import project.camus.webmvc.common.ResponseWrapper;

@RestController
@RequiredArgsConstructor
public class TaskCreateController extends V1TaskController {

    private final TaskCreateDelegator taskCreateDelegator;

    @PostMapping()
    public ResponseEntity<SuccessResponse<CreateTaskResponse>> createTask(@Valid @RequestBody CreateTaskRequest request) {

        return ResponseWrapper.success(taskCreateDelegator.createTask(request));
    }
}
