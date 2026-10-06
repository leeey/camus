package project.camus.jwt.webflux.api.router;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import project.camus.common.SuccessResponse;
import project.camus.jwt.webflux.api.handler.GetMeHandler;
import project.camus.webflux.common.ResponseWrapper;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UserRouter {

    private final GetMeHandler getMeHandler;

    @RouterOperations({
        @RouterOperation(path = "/users/me", produces = {
            MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET,
            beanClass = GetMeHandler.class, beanMethod = "handle",
            operation = @Operation(operationId = "getMe",
                tags = {"user"},
                responses = {
                    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
                    @ApiResponse(responseCode = "401", description = "UNAUTHORIZED")
                }
            )
        )
    })
    @Bean
    public RouterFunction<ServerResponse> jwtRouterFunction() {

        return RouterFunctions.route()
            .path("/users", builder -> builder
                .GET("/me", getMeHandler))
            .onError(Exception.class, (exception, request) -> ResponseWrapper.fail(exception))
            .build();
    }
}
