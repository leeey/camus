package project.camus.springcloud.gateway.fallback;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.camus.common.FailureResponse;
import reactor.core.publisher.Mono;

/**
 * CircuitBreaker 필터의 fallbackUri(forward:/fallback/{service}) 대상.
 * 하위 서비스 장애(5xx, timeout, circuit open)를 표준 실패 응답(503)으로 바꾼다.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/{service}")
    public Mono<ResponseEntity<FailureResponse<String>>> fallback(@PathVariable("service") String service) {

        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(FailureResponse.<String>builder()
                .timestamp(System.currentTimeMillis())
                .errors(List.of(service + " is unavailable"))
                .build()));
    }
}
