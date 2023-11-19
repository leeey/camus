package project.camus.hexagonal.web.controller.task;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import project.camus.common.SuccessResponse;
import project.camus.hexagonal.web.controller.task.delegator.TaskArchiveDelegator;
import project.camus.hexagonal.web.controller.task.response.ArchiveTaskResponse;
import project.camus.webmvc.common.ResponseWrapper;

@RestController
@RequiredArgsConstructor
public class TaskArchiveController extends V1TaskController {

    private final TaskArchiveDelegator taskArchiveDelegator;

    @PutMapping("/{id}/archive")
    public ResponseEntity<SuccessResponse<ArchiveTaskResponse>> archiveTaskById(@PathVariable("id") @Valid @NotNull Long id) {

        return ResponseWrapper.success(taskArchiveDelegator.archiveTaskById(id));
    }
}
