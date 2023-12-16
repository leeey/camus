package project.camus.jwt.webflux.exception;

import java.util.List;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import project.camus.common.FailureResponse;
import project.camus.common.exception.CamusClientException;
import project.camus.common.exception.CamusServerException;
import project.camus.common.util.ObjectMapperUtil;
import reactor.core.publisher.Mono;

@Order(-2)
@Component
public class ExceptionHandler implements ErrorWebExceptionHandler {

    @NonNull
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, @NonNull Throwable throwable) {

        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        if (throwable instanceof CamusServerException camusServerException) {
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return toResponse(exchange, bufferFactory, failure(camusServerException));
        }

        if (throwable instanceof CamusClientException camusClientException) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return toResponse(exchange, bufferFactory, failure(camusClientException));
        }

        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return toResponse(exchange, bufferFactory, failure(throwable));
    }

    private static Mono<Void> toResponse(ServerWebExchange exchange, DataBufferFactory bufferFactory, FailureResponse<Object> camusServerException) {

        return exchange.getResponse().writeWith(Mono.just(bufferFactory.wrap(ObjectMapperUtil
            .writeValueAsBytes(camusServerException))));
    }

    private static FailureResponse<Object> failure(Throwable throwable) {

        return FailureResponse.builder()
            .timestamp(System.currentTimeMillis())
            .errors(List.of(throwable.getMessage()))
            .build();
    }
}
