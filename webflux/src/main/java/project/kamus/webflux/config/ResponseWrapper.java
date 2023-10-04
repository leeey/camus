package project.kamus.webflux.config;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;
import project.kamus.common.FailureResponse;
import project.kamus.common.SuccessResponse;
import project.kamus.common.exception.KamusServerException;
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

    if (exception instanceof KamusServerException kamusServerException) {
      return ServerResponse.status(kamusServerException.getHttpStatus())
          .bodyValue(FailureResponse.builder()
              .timestamp(System.currentTimeMillis())
              .errors(List.of(kamusServerException.getMessage())).build());
    }

    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .bodyValue(FailureResponse.builder()
            .timestamp(System.currentTimeMillis())
            .errors(List.of(exception.getMessage())).build());
  }
}
