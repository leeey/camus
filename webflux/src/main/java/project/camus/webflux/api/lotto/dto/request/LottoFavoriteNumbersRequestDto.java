package project.camus.webflux.api.lotto.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.camus.common.exception.CamusClientException;
import project.camus.common.util.ValidationUtil;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LottoFavoriteNumbersRequestDto {

    @Size(max = 5)
    private Set<Integer> favoriteNumbers;

    @Max(10)
    @Min(1)
    private Integer resultCount;

    public void validate() {

        ValidationUtil.validate(this);
        favoriteNumbers.forEach(num -> {
            if (num < 1 || num > 45) {
                throw new CamusClientException(new IllegalArgumentException("favoriteNumbers is invalid."));
            }
        });
    }
}
