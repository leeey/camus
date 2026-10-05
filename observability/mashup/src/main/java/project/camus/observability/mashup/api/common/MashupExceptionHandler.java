package project.camus.observability.mashup.api.common;

import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.camus.common.FailureResponse;
import project.camus.common.exception.CamusClientException;
import project.camus.observability.mashup.domain.client.config.DownstreamUnavailableException;

@Slf4j
@RestControllerAdvice
public class MashupExceptionHandler {

    /**
     * 하위 서비스 장애 (5xx, 연결 실패/timeout, circuit breaker open)
     */
    @ExceptionHandler({DownstreamUnavailableException.class, RetryableException.class, CallNotPermittedException.class})
    public ResponseEntity<FailureResponse<String>> handleDownstreamUnavailable(Exception e) {

        log.warn("downstream unavailable: {}", e.toString());
        return failure(HttpStatus.SERVICE_UNAVAILABLE, "downstream service is unavailable");
    }

    @ExceptionHandler(CamusClientException.class)
    public ResponseEntity<FailureResponse<String>> handleClientError(CamusClientException e) {

        return failure(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    private ResponseEntity<FailureResponse<String>> failure(HttpStatus status, String message) {

        return ResponseEntity.status(status)
            .body(FailureResponse.<String>builder()
                .timestamp(System.currentTimeMillis())
                .errors(List.of(message))
                .build());
    }
}
