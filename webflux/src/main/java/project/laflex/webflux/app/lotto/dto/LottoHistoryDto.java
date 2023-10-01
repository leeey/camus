package project.laflex.webflux.app.lotto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LottoHistoryDto {

  private Integer round;

  private String date;

  private Integer no1;

  private Integer no2;

  private Integer no3;

  private Integer no4;

  private Integer no5;

  private Integer no6;

  private Integer noBonus;
}
