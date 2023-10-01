package project.laflex.lotto.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseWrapper {

  public static <T> Mono<ServerResponse> of(T result) {

    return ServerResponse.ok()
        .bodyValue(SuccessResponse.builder()
            .timestamp(System.currentTimeMillis())
            .result(result)
            .build());
  }
}
