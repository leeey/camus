package project.camus.hexagonal.web.task.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import project.camus.hexagonal.web.common.ResponseWrapper;
import project.camus.hexagonal.web.common.SuccessResponse;
import project.camus.hexagonal.web.task.controller.response.CreateTaskResponse;
import project.camus.hexagonal.web.task.delegator.TaskDeleteDelegator;

@RestController
@RequiredArgsConstructor
public class TaskDeleteController extends V1TaskController {

    private final TaskDeleteDelegator taskDeleteDelegator;

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<CreateTaskResponse>> deleteTaskById(@PathVariable("id") @Valid @NotNull Long id) {

        taskDeleteDelegator.deleteTaskById(id);
        return ResponseWrapper.ok();
    }
}
