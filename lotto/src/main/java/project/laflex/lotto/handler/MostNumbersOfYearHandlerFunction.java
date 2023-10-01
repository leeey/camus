package project.laflex.lotto.handler;

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
import project.laflex.lotto.config.ResponseWrapper;
import project.laflex.lotto.dto.HistoryDto;
import project.laflex.lotto.dto.MostNumbersOfYearDto;
import project.laflex.lotto.dto.NumberCountDto;
import project.laflex.lotto.util.HistoryUtil;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class MostNumbersOfYearHandlerFunction implements HandlerFunction<ServerResponse> {

  @NonNull
  @Override
  public Mono<ServerResponse> handle(@NonNull ServerRequest request) {

    List<HistoryDto> histories = HistoryUtil.getHistories();
    List<MostNumbersOfYearDto> result = new ArrayList<>();

    IntStream.rangeClosed(2002, Year.now().getValue())
        .forEach(year -> {
          Map<Integer, List<Integer>> numberCountMap = getNumberCountMap(year, histories);
          List<NumberCountDto> numberCountDtoList = getNumberCountDtoList(numberCountMap);
          List<Integer> mostNumbers = getMostNumbersOfYear(numberCountDtoList);
          result.add(MostNumbersOfYearDto.builder().year(year).numbers(mostNumbers).build());
        });

    return ResponseWrapper.of(result);
  }

  private Map<Integer, List<Integer>> getNumberCountMap(int year, List<HistoryDto> histories) {
    return histories.stream()
        .filter(dto -> dto.getDate().startsWith(year + "-"))
        .map(dto -> List.of(dto.getNo1(), dto.getNo2(), dto.getNo3(), dto.getNo4(), dto.getNo5(), dto.getNo6(), dto.getNoBonus()))
        .flatMap(numbers -> Arrays.stream(numbers.toArray(Integer[]::new)))
        .collect(Collectors.groupingBy(Integer::intValue));
  }

  private List<NumberCountDto> getNumberCountDtoList(Map<Integer, List<Integer>> map) {
    return map.entrySet()
        .stream()
        .map(entry -> NumberCountDto.builder().number(entry.getKey()).count(entry.getValue().size()).build())
        .sorted((a, b) -> b.getCount().compareTo(a.getCount()))
        .toList();
  }

  private List<Integer> getMostNumbersOfYear(List<NumberCountDto> numberCountMapList) {
    return numberCountMapList.subList(0, 6).stream()
        .map(NumberCountDto::getNumber)
        .sorted()
        .toList();
  }
}
