package project.camus.database.redis.handler.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import project.camus.common.exception.CamusServerException;
import project.camus.database.redis.model.user.User;
import project.camus.webflux.common.ResponseWrapper;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetUserHandler implements HandlerFunction<ServerResponse> {

    private final ReactiveRedisOperations<String, User> operations;

    @Value("${redis.key-prefix.user}")
    private String keyPrefix;

    @NonNull
    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {

        String userId = request.pathVariable("userId");

        return operations.opsForValue().get(keyPrefix + userId)
            .flatMap(ResponseWrapper::success)
            .switchIfEmpty(Mono.error(new CamusServerException("no user by userId : " + userId)));
    }
}
