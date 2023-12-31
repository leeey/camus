package project.camus.database.redis.router.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
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
import project.camus.database.redis.handler.user.CreateUserHandler;
import project.camus.database.redis.handler.user.GetUserHandler;
import project.camus.database.redis.model.user.User;
import project.camus.webflux.common.ResponseWrapper;

@Configuration
@RequiredArgsConstructor
public class UserRouter {

    private final CreateUserHandler createUserHandler;

    private final GetUserHandler getUserHandler;

    @RouterOperations({
        @RouterOperation(path = "/redis/users"
            , produces = {
            MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.POST,
            beanClass = CreateUserHandler.class, beanMethod = "handle",
            operation = @Operation(operationId = "createUser",
                tags = {"Redis User"},
                responses = {
                    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
                    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = FailureResponse.class))),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = FailureResponse.class)))},
                requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = User.class))))
        ),
        @RouterOperation(path = "/redis/users/{userId}"
            , produces = {
            MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET,
            beanClass = GetUserHandler.class, beanMethod = "handle",
            operation = @Operation(operationId = "getUser",
                tags = {"Redis User"},
                parameters = {
                    @Parameter(name = "userId", in = ParameterIn.PATH, style = ParameterStyle.SIMPLE, required = true)
                },
                responses = {
                    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
                    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = FailureResponse.class))),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = FailureResponse.class)))})
        ),
    })
    @Bean
    public RouterFunction<ServerResponse> userRouteFunction() {

        return RouterFunctions.route()
            .path("/redis/users", builder -> builder
                .nest(RequestPredicates.accept(MediaType.APPLICATION_JSON), nestBuilder -> nestBuilder
                    .POST(createUserHandler)
                    .GET("/{userId}", getUserHandler)))
            .onError(Exception.class, (exception, request) -> ResponseWrapper.fail(exception))
            .build();
    }
}
