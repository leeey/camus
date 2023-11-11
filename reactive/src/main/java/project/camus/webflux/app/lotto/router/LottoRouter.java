package project.camus.webflux.app.lotto.router;

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
import project.camus.webflux.app.lotto.dto.request.LottoFavoriteNumbersRequestDto;
import project.camus.webflux.app.lotto.handler.LottoFavoriteNumbersHandler;
import project.camus.webflux.app.lotto.handler.LottoYearlyMostNumbersHandler;
import project.camus.webflux.config.ResponseWrapper;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LottoRouter {

    private final LottoFavoriteNumbersHandler favoriteNumbersHandler;

    private final LottoYearlyMostNumbersHandler mostNumbersOfYearHandler;

    @RouterOperations({
        @RouterOperation(path = "/lotto/favorite-nums"
            , produces = {
            MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.POST,
            beanClass = LottoFavoriteNumbersHandler.class, beanMethod = "handle",
            operation = @Operation(operationId = "getFavoriteNumbersAndRandom",
                tags = {"Lotto"},
                responses = {
                    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
                    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = FailureResponse.class))),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = FailureResponse.class)))},
                requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = LottoFavoriteNumbersRequestDto.class))))
        ),
        @RouterOperation(path = "/lotto/most-numbers-of-year"
            , produces = {
            MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET,
            beanClass = LottoYearlyMostNumbersHandler.class, beanMethod = "handle",
            operation = @Operation(operationId = "getMostNumbersOfYear",
                tags = {"Lotto"},
                responses = {
                    @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = SuccessResponse.class))),
                    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = FailureResponse.class))),
                    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = FailureResponse.class)))})
        )
    })
    @Bean
    public RouterFunction<ServerResponse> lottoRouterFunction() {

        return RouterFunctions.route()
            .path("/lotto", builder -> builder
                .nest(RequestPredicates.accept(MediaType.APPLICATION_JSON), nestBuilder -> nestBuilder
                    .POST("/favorite-nums", favoriteNumbersHandler)
                    .GET("/most-numbers-of-year", mostNumbersOfYearHandler)))
            .onError(Exception.class, (exception, request) -> ResponseWrapper.fail(exception))
            .build();
    }
}
