package project.camus.webflux.app.auth.jwt.router;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import project.camus.common.FailureResponse;
import project.camus.common.SuccessResponse;
import project.camus.webflux.app.auth.jwt.dto.request.JwtCreateTokenRequestDto;
import project.camus.webflux.app.auth.jwt.handler.JwtCreateTokenHandler;
import project.camus.webflux.app.auth.jwt.handler.JwtGetUsernameHandler;
import project.camus.webflux.config.ResponseWrapper;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JwtRouter {

    private final JwtCreateTokenHandler jwtCreateTokenHandler;

    private final JwtGetUsernameHandler jwtGetUsernameHandler;

    @RouterOperations({
        @RouterOperation(path = "/jwt/tokens", produces = {
            MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.POST,
            beanClass = JwtCreateTokenHandler.class, beanMethod = "handle",
            operation = @Operation(operationId = "createJwtToken",
                tags = {"JWT"},
                responses = {
                    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
                    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = FailureResponse.class))),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = FailureResponse.class)))},
                requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = JwtCreateTokenRequestDto.class))))
        ),
        @RouterOperation(path = "/jwt/username", produces = {
            MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET,
            beanClass = JwtGetUsernameHandler.class, beanMethod = "handle",
            operation = @Operation(operationId = "getJwtUsername",
                tags = {"JWT"},
                responses = {
                    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
                    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = FailureResponse.class))),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = FailureResponse.class)))
                }
            )
        )
    })
    @Bean
    public RouterFunction<ServerResponse> jwtRouterFunction() {

        return RouterFunctions.route()
            .path("/jwt", builder -> builder
                .nest(RequestPredicates.accept(MediaType.APPLICATION_JSON), nestBuilder -> nestBuilder
                    .POST("/tokens", jwtCreateTokenHandler)
                    .GET("/username", jwtGetUsernameHandler)))
            .onError(Exception.class, (exception, request) -> ResponseWrapper.fail(exception))
            .build();
    }
}
