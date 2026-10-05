package project.camus.observability.mashup.api.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.camus.observability.mashup.domain.client.TaskFeignClient;
import project.camus.observability.mashup.domain.dto.task.TaskDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskFeignClient taskFeignClient;

    /**
     * task 는 부가 데이터라 장애 시 비어 있는 결과로 대체한다 (부분 응답).
     * <p>
     * fallback 을 @CircuitBreaker 에 두면 실패가 fallback 으로 바뀌어 재시도가 일어나지 않으므로, 가장 바깥인 @Retry 에 둔다.
     *
     * @return 장애로 조회하지 못하면 Optional.empty()
     */
    @Retry(name = "task", fallbackMethod = "findTasksFallback")
    @CircuitBreaker(name = "task")
    public Optional<List<TaskDto>> findTasksByMemberId(Long memberId) {

        return Optional.of(taskFeignClient.findTasksByMemberId(memberId).getResult());
    }

    private Optional<List<TaskDto>> findTasksFallback(Long memberId, Throwable throwable) {

        log.warn("task service is unavailable. return partial response. memberId={}, cause={}", memberId,
            throwable.toString());
        return Optional.empty();
    }
}
