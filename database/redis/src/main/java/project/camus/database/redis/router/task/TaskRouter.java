package project.camus.database.redis.router.task;

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
import project.camus.database.redis.handler.task.CreateTaskHandler;
import project.camus.database.redis.handler.task.GetTaskHandler;
import project.camus.database.redis.model.task.Task;
import project.camus.webflux.common.ResponseWrapper;

@Configuration
@RequiredArgsConstructor
public class TaskRouter {

    private final CreateTaskHandler createTaskHandler;

    private final GetTaskHandler getTaskHandler;

    @RouterOperations({
        @RouterOperation(path = "/redis/tasks"
            , produces = {
            MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.POST,
            beanClass = CreateTaskHandler.class, beanMethod = "handle",
            operation = @Operation(operationId = "createTask",
                tags = {"Redis Task"},
                responses = {
                    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
                    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = FailureResponse.class))),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = FailureResponse.class)))},
                requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = Task.class))))
        ),
        @RouterOperation(path = "/redis/tasks/{taskId}"
            , produces = {
            MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET,
            beanClass = GetTaskHandler.class, beanMethod = "handle",
            operation = @Operation(operationId = "getTask",
                tags = {"Redis Task"},
                parameters = {
                    @Parameter(name = "taskId", in = ParameterIn.PATH, style = ParameterStyle.SIMPLE, required = true)
                },
                responses = {
                    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
                    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = FailureResponse.class))),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = FailureResponse.class)))})
        ),
    })
    @Bean
    public RouterFunction<ServerResponse> taskRouteFunction() {

        return RouterFunctions.route()
            .path("/redis/tasks", builder -> builder
                .nest(RequestPredicates.accept(MediaType.APPLICATION_JSON), nestBuilder -> nestBuilder
                    .POST(createTaskHandler)
                    .GET("/{taskId}", getTaskHandler)))
            .onError(Exception.class, (exception, request) -> ResponseWrapper.fail(exception))
            .build();
    }
}
