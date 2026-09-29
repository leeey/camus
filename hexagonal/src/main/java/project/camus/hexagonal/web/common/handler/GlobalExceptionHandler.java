package project.camus.hexagonal.web.common.handler;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.camus.common.FailureResponse;
import project.camus.hexagonal.domain.task.exception.TaskNotFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<FailureResponse<String>> handleTaskNotFound(TaskNotFoundException e) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(FailureResponse.<String>builder()
                .timestamp(System.currentTimeMillis())
                .errors(List.of(e.getMessage()))
                .build());
    }
}
