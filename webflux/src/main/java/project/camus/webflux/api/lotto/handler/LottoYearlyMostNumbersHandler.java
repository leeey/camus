package project.camus.webflux.api.lotto.handler;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import project.camus.reactive.api.lotto.dto.response.LottoYearlyMostNumbersResponseDto;
import project.camus.reactive.common.ResponseWrapper;
import project.camus.webflux.api.lotto.dto.LottoHistoryDto;
import project.camus.webflux.api.lotto.dto.LottoNumberCountDto;
import project.camus.webflux.api.lotto.util.LottoHistoryUtil;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LottoYearlyMostNumbersHandler implements HandlerFunction<ServerResponse> {

    @NonNull
    @Override
    public Mono<ServerResponse> handle(@NonNull ServerRequest request) {

        List<LottoHistoryDto> histories = LottoHistoryUtil.getHistories();
        List<LottoYearlyMostNumbersResponseDto> result = new ArrayList<>();

        IntStream.rangeClosed(2002, Year.now().getValue())
            .forEach(year -> {
                Map<Integer, List<Integer>> numberCountMap = getNumberCountMap(year, histories);
                List<LottoNumberCountDto> numberCountDtoList = getNumberCountDtoList(numberCountMap);
                List<Integer> mostNumbers = getMostNumbersOfYear(numberCountDtoList);
                result.add(LottoYearlyMostNumbersResponseDto.builder().year(year).numbers(mostNumbers).build());
            });

        return ResponseWrapper.success(result);
    }

    private Map<Integer, List<Integer>> getNumberCountMap(int year, List<LottoHistoryDto> histories) {

        return histories.stream()
            .filter(dto -> dto.getDate().startsWith(year + "-"))
            .map(dto -> List.of(dto.getNo1(), dto.getNo2(), dto.getNo3(), dto.getNo4(), dto.getNo5(), dto.getNo6(), dto.getNoBonus()))
            .flatMap(numbers -> Arrays.stream(numbers.toArray(Integer[]::new)))
            .collect(Collectors.groupingBy(Integer::intValue));
    }

    private List<LottoNumberCountDto> getNumberCountDtoList(Map<Integer, List<Integer>> map) {

        return map.entrySet()
            .stream()
            .map(entry -> LottoNumberCountDto.builder().number(entry.getKey()).count(entry.getValue().size()).build())
            .sorted((a, b) -> b.getCount().compareTo(a.getCount()))
            .toList();
    }

    private List<Integer> getMostNumbersOfYear(List<LottoNumberCountDto> numberCountMapList) {

        return numberCountMapList.subList(0, 6).stream()
            .map(LottoNumberCountDto::getNumber)
            .sorted()
            .toList();
    }
}
