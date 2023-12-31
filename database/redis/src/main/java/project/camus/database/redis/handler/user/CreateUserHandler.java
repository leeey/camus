package project.camus.database.redis.handler.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import project.camus.database.redis.model.user.User;
import project.camus.webflux.common.ResponseWrapper;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreateUserHandler implements HandlerFunction<ServerResponse> {

    private final ReactiveRedisOperations<String, User> operations;

    @Value("${redis.key-prefix.user}")
    private String keyPrefix;

    @NonNull
    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {

        return request.bodyToMono(User.class)
            .flatMap(user -> operations.opsForValue().set(keyPrefix + user.getId(), user))
            .flatMap(ResponseWrapper::success);
    }
}
