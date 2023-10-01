package project.laflex.lotto.router;

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
import project.laflex.lotto.config.FailureResponse;
import project.laflex.lotto.config.ResponseWrapper;
import project.laflex.lotto.config.SuccessResponse;
import project.laflex.lotto.dto.FavoriteNumbersAndRandomDto;
import project.laflex.lotto.handler.FavoriteNumbersHandlerFunction;
import project.laflex.lotto.handler.HealthCheckHandlerFunction;
import project.laflex.lotto.handler.MostNumbersOfYearHandlerFunction;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LottoRouterFunction {

  private final HealthCheckHandlerFunction healthCheckHandlerFunction;

  private final FavoriteNumbersHandlerFunction favoriteNumbersHandlerFunction;

  private final MostNumbersOfYearHandlerFunction mostNumbersOfYearHandlerFunction;

  @RouterOperations({
      @RouterOperation(path = "/health"
          , produces = {
          MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET, beanClass = HealthCheckHandlerFunction.class, beanMethod = "handle",
          operation = @Operation(operationId = "healthCheck",
              tags = {"HealthCheck"},
              responses = {
                  @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
                  @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = FailureResponse.class))),
                  @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = FailureResponse.class)))})
      ),
      @RouterOperation(path = "/lotto/favorite-nums"
          , produces = {
          MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.POST,
          beanClass = FavoriteNumbersHandlerFunction.class, beanMethod = "handle",
          operation = @Operation(operationId = "getFavoriteNumbersAndRandom",
              tags = {"API"},
              responses = {
                  @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
                  @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = FailureResponse.class))),
                  @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = FailureResponse.class)))},
              requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = FavoriteNumbersAndRandomDto.class))))
      ),
      @RouterOperation(path = "/lotto/most-numbers-of-year"
          , produces = {
          MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET,
          beanClass = MostNumbersOfYearHandlerFunction.class, beanMethod = "handle",
          operation = @Operation(operationId = "getMostNumbersOfYear",
              tags = {"API"},
              responses = {
                  @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
                  @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = FailureResponse.class))),
                  @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = FailureResponse.class)))})
      )
  })
  @Bean
  public RouterFunction<ServerResponse> route() {

    return RouterFunctions.route()
        .path("/health", builder -> builder
            .GET(RequestPredicates.accept(MediaType.APPLICATION_JSON), healthCheckHandlerFunction))
        .path("/lotto", builder -> builder
            .nest(RequestPredicates.accept(MediaType.APPLICATION_JSON), nestBuilder -> nestBuilder
                .GET("/health", healthCheckHandlerFunction)
                .POST("/favorite-nums", favoriteNumbersHandlerFunction)
                .GET("/most-numbers-of-year", mostNumbersOfYearHandlerFunction)))
        .onError(Exception.class, (exception, request) -> ResponseWrapper.fail(exception))
        .build();
  }
}
