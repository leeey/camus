package project.laflex.lotto.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MostNumbersOfYearDto {

  private Integer year;

  private List<Integer> numbers;
}
