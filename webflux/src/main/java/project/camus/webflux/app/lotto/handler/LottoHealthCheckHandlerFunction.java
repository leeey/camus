package project.camus.webflux.app.lotto.handler;

import java.util.Map;
import project.camus.webflux.config.ResponseWrapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class LottoHealthCheckHandlerFunction implements HandlerFunction<ServerResponse> {

  @NonNull
  @Override
  public Mono<ServerResponse> handle(@NonNull ServerRequest request) {

    return ResponseWrapper.success(Map.of("status", "OK"));
  }
}
