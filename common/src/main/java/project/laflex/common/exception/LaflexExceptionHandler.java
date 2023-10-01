package project.laflex.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(-2)
@Slf4j
@Component
public class LaflexExceptionHandler implements ErrorWebExceptionHandler {

  @NonNull
  @Override
  public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {

    //TODO: Error Handling
    return Mono.empty();
  }
}
