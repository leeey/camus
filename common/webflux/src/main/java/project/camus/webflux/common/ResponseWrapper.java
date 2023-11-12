package project.camus.webflux.common;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;
import project.camus.common.FailureResponse;
import project.camus.common.SuccessResponse;
import project.camus.common.exception.CamusClientException;
import project.camus.common.exception.CamusServerException;
import reactor.core.publisher.Mono;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseWrapper {

    public static <T> Mono<ServerResponse> success(T result) {

        return ServerResponse.ok()
            .bodyValue(SuccessResponse.builder()
                .timestamp(System.currentTimeMillis())
                .result(result)
                .build());
    }

    public static Mono<ServerResponse> fail(Throwable exception) {

        if (exception instanceof CamusClientException camusClientException) {
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .bodyValue(FailureResponse.builder()
                    .timestamp(System.currentTimeMillis())
                    .errors(List.of(camusClientException.getMessage())).build());
        }

        if (exception instanceof CamusServerException camusServerException) {
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValue(FailureResponse.builder()
                    .timestamp(System.currentTimeMillis())
                    .errors(List.of(camusServerException.getMessage())).build());
        }

        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .bodyValue(FailureResponse.builder()
                .timestamp(System.currentTimeMillis())
                .errors(List.of(exception.getMessage())).build());
    }
}
