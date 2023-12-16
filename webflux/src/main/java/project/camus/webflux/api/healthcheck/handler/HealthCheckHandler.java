package project.camus.webflux.api.healthcheck.handler;

import java.util.Map;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import project.camus.reactive.common.ResponseWrapper;
import reactor.core.publisher.Mono;

@Component
public class HealthCheckHandler implements HandlerFunction<ServerResponse> {

    @NonNull
    @Override
    public Mono<ServerResponse> handle(@NonNull ServerRequest request) {

        return ResponseWrapper.success(Map.of("status", "OK"));
    }
}
