package project.camus.hexagonal.web.task.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.camus.hexagonal.web.common.ResponseWrapper;
import project.camus.hexagonal.web.common.SuccessResponse;
import project.camus.hexagonal.web.task.controller.request.CreateTaskRequest;
import project.camus.hexagonal.web.task.controller.response.CreateTaskResponse;
import project.camus.hexagonal.web.task.delegator.TaskCreateDelegator;

@RestController
@RequiredArgsConstructor
public class TaskCreateController extends V1TaskController {

    private final TaskCreateDelegator taskCreateDelegator;

    @PostMapping()
    public ResponseEntity<SuccessResponse<CreateTaskResponse>> createTask(@Valid @RequestBody CreateTaskRequest request) {

        return ResponseWrapper.ok(taskCreateDelegator.createTask(request));
    }
}
