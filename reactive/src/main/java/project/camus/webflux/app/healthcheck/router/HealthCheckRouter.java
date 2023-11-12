package project.camus.webflux.app.healthcheck.router;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import project.camus.webflux.app.healthcheck.handler.HealthCheckHandler;
import project.camus.webflux.common.ResponseWrapper;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class HealthCheckRouter {

    private final HealthCheckHandler healthCheckHandler;

    @Bean
    public RouterFunction<ServerResponse> healthCheckRouterFunction() {

        return RouterFunctions.route()
            .GET("/healthcheck", healthCheckHandler)
            .onError(Exception.class, (exception, request) -> ResponseWrapper.fail(exception))
            .build();
    }
}