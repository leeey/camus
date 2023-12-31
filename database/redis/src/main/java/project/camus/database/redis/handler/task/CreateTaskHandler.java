package project.camus.database.redis.handler.task;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import project.camus.database.redis.model.task.Task;
import project.camus.webflux.common.ResponseWrapper;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreateTaskHandler implements HandlerFunction<ServerResponse> {

    private final ReactiveRedisOperations<String, Task> operations;

    @Value("${redis.key-prefix.task}")
    private String keyPrefix;

    @NonNull
    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {

        return request.bodyToMono(Task.class)
            .flatMap(task -> operations.opsForValue().set(keyPrefix + task.getId(), task))
            .flatMap(ResponseWrapper::success);
    }
}
