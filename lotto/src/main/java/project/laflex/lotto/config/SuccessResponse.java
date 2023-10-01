package project.laflex.lotto.config;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SuccessResponse<T> {

  private Long timestamp;

  private T result;
}
