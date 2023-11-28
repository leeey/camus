package project.camus.kafka.producer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.camus.common.SuccessResponse;
import project.camus.kafka.producer.controller.request.TaskRequest;
import project.camus.kafka.producer.service.TaskService;
import project.camus.webmvc.common.ResponseWrapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/camus/kafka/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public <T> ResponseEntity<SuccessResponse<T>> send(@RequestBody TaskRequest request) {

        taskService.sendMessage(request);
        return ResponseWrapper.success();
    }
}
