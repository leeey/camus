package leeey.project.lotto.handler;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import leeey.project.lotto.config.ResponseWrapper;
import leeey.project.lotto.dto.HistoryDto;
import leeey.project.lotto.util.HistoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class MostPickedNumbersOfYearHandlerFunction implements HandlerFunction<ServerResponse> {

  @NonNull
  @Override
  public Mono<ServerResponse> handle(@NonNull ServerRequest request) {

    List<HistoryDto> histories = HistoryUtil.getHistories();
    List<Map<Integer, List<Integer>>> result = new ArrayList<>();

    final String numberKey = "number";
    final String countKey = "count";

    IntStream.rangeClosed(2002, Year.now().getValue())
        .forEach(year -> {
          List<Integer> list = histories.stream()
              .filter(dto -> dto.getDate().startsWith(year + "-"))
              .map(dto -> List.of(dto.getNo1(), dto.getNo2(), dto.getNo3(), dto.getNo4(), dto.getNo5(), dto.getNo6(), dto.getNoBonus()))
              .flatMap(numbers -> Arrays.stream(numbers.toArray(Integer[]::new)))
              .collect(Collectors.groupingBy(Integer::intValue))
              .entrySet()
              .stream()
              .map(entry -> Map.of(numberKey, entry.getKey(), countKey, entry.getValue().size()))
              .sorted((a, b) -> b.get(countKey).compareTo(a.get(countKey)))
              .toList()
              .subList(0, 6).stream()
              .map(map -> map.get(numberKey))
              .sorted()
              .toList();

          result.add(Map.of(year, list));
        });

    return ResponseWrapper.of(result);
  }
}
