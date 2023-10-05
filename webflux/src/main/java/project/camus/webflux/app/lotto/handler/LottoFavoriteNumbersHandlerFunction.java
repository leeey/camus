package project.camus.webflux.app.lotto.handler;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import project.camus.webflux.config.ResponseWrapper;
import project.camus.webflux.app.lotto.dto.LottoFavoriteNumbersAndRandomDto;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LottoFavoriteNumbersHandlerFunction implements HandlerFunction<ServerResponse> {

  @NonNull
  @Override
  public Mono<ServerResponse> handle(@NonNull ServerRequest request) {

    return request.bodyToMono(LottoFavoriteNumbersAndRandomDto.class)
        .doOnNext(LottoFavoriteNumbersAndRandomDto::validate)
        .flatMap(dto -> {
          Set<Set<Integer>> result = new HashSet<>();
          while (result.size() < dto.getResultCount()) {
            Set<Integer> set = new TreeSet<>(dto.getFavoriteNumbers());
            while (set.size() < 6) {
              int r = (int) (Math.random() * 45) + 1;
              set.add(r);
            }
            if (result.contains(set)) {
              log.warn("same result is exist... " + set);
            }
            result.add(set);
          }
          log.info("results size : " + result.size());
          return ResponseWrapper.success(result);
        });
  }
}
