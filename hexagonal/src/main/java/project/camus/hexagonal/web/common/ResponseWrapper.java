package project.camus.hexagonal.web.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.util.CastUtils;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseWrapper {

    public static <T> ResponseEntity<SuccessResponse<T>> ok(T data) {

        return ResponseEntity.ok(CastUtils.cast(SuccessResponse.builder()
            .data(data)
            .timestamp(System.currentTimeMillis())
            .build()));
    }

    public static <T> ResponseEntity<SuccessResponse<T>> ok() {

        return ResponseEntity.ok().build();
    }
}
