package project.camus.webflux.api.lotto.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LottoYearlyMostNumbersResponseDto {

  private Integer year;

  private List<Integer> numbers;
}
