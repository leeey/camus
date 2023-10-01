package project.laflex.lotto.config;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FailureResponse<T> {

  private Long timestamp;

  private List<T> errors;
}