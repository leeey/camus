package project.camus.webflux.app.lotto.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LottoMostNumbersOfYearDto {

  private Integer year;

  private List<Integer> numbers;
}
