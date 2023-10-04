package project.kamus.webflux.app.lotto.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LottoNumberCountDto {

  private Integer number;

  private Integer count;
}
