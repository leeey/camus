package project.camus.hexagonal.web.common;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SuccessResponse<T> {

    private T data;

    private Long timestamp;
}
