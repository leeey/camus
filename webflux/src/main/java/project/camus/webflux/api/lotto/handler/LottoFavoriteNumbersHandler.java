package project.camus.webflux.api.lotto.handler;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import project.camus.common.util.RandomUtil;
import project.camus.webflux.common.ResponseWrapper;
import project.camus.webflux.api.lotto.dto.request.LottoFavoriteNumbersRequestDto;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LottoFavoriteNumbersHandler implements HandlerFunction<ServerResponse> {

    @NonNull
    @Override
    public Mono<ServerResponse> handle(@NonNull ServerRequest request) {

        return request.bodyToMono(LottoFavoriteNumbersRequestDto.class)
            .doOnNext(LottoFavoriteNumbersRequestDto::validate)
            .flatMap(dto -> {
                Set<Set<Integer>> result = new HashSet<>();
                while (result.size() < dto.getResultCount()) {
                    Set<Integer> set = new TreeSet<>(dto.getFavoriteNumbers());
                    while (set.size() < 6) {
                        set.add(RandomUtil.nextInt(45) + 1);
                    }
                    if (result.contains(set)) {
                        log.warn("same result is exist... " + set);
                    }
                    result.add(set);
                }
                return ResponseWrapper.success(result);
            });
    }
}
