package project.camus.hexagonal.web.task.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import project.camus.hexagonal.web.common.ResponseWrapper;
import project.camus.hexagonal.web.common.SuccessResponse;
import project.camus.hexagonal.web.task.controller.response.ArchiveTaskResponse;
import project.camus.hexagonal.web.task.delegator.TaskArchiveDelegator;

@RestController
@RequiredArgsConstructor
public class TaskArchiveController extends V1TaskController {

    private final TaskArchiveDelegator taskArchiveDelegator;

    @PutMapping("/{id}/archive")
    public ResponseEntity<SuccessResponse<ArchiveTaskResponse>> archiveTaskById(@PathVariable("id") @Valid @NotNull Long id) {

        return ResponseWrapper.ok(taskArchiveDelegator.archiveTaskById(id));
    }
}
