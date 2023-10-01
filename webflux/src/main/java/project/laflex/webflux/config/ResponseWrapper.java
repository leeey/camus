package project.laflex.webflux.config;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;
import project.laflex.common.FailureResponse;
import project.laflex.common.SuccessResponse;
import project.laflex.common.exception.LaflexServerException;
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

  public static Mono<ServerResponse> fail(Exception exception) {

    if (exception instanceof LaflexServerException laflexException) {
      return ServerResponse.status(laflexException.getHttpStatus())
          .bodyValue(FailureResponse.builder()
              .timestamp(System.currentTimeMillis())
              .errors(List.of(laflexException.getMessage())).build());
    }

    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .bodyValue(FailureResponse.builder()
            .timestamp(System.currentTimeMillis())
            .errors(List.of(exception.getMessage())).build());
  }
}
