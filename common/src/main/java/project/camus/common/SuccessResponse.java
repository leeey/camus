package project.camus.common;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SuccessResponse<T> {

  private Long timestamp;

  private T result;
}
