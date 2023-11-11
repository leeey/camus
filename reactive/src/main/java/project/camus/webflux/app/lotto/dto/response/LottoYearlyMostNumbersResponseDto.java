package project.camus.webflux.app.lotto.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LottoYearlyMostNumbersResponseDto {

  private Integer year;

  private List<Integer> numbers;
}
