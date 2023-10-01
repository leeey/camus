package project.laflex.webflux.app.lotto.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.laflex.common.exception.LaflexClientException;
import project.laflex.common.util.ValidationUtil;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LottoFavoriteNumbersAndRandomDto {

  @Size(min = 1, max = 5)
  @NotEmpty
  private Set<Integer> favoriteNumbers;

  @Max(10)
  @Min(1)
  private Integer resultCount;

  public void validate() {

    ValidationUtil.validate(this);
    favoriteNumbers.forEach(num -> {
      if (num < 1 || num > 45) {
        throw new LaflexClientException(new IllegalArgumentException("favoriteNumbers is invalid."));
      }
    });
  }
}
