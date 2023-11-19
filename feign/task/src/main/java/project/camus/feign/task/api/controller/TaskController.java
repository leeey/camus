package project.camus.feign.task.api.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.camus.common.SuccessResponse;
import project.camus.feign.task.api.controller.delegator.TaskDelegator;
import project.camus.feign.task.domain.dto.TaskDto;
import project.camus.webmvc.common.ResponseWrapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/camus/v1/tasks")
public class TaskController {

    private final TaskDelegator taskDelegator;

    @GetMapping()
    public ResponseEntity<SuccessResponse<List<TaskDto>>> findTasksByMemberId(@RequestParam("memberId") Long memberId) {

        return ResponseWrapper.success(taskDelegator.findTasksByMemberId(memberId));
    }
}
