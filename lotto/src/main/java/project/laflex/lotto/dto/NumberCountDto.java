package project.laflex.lotto.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NumberCountDto {

  private Integer number;

  private Integer count;
}
