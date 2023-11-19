package project.camus.webmvc.common;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.util.CastUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import project.camus.common.SuccessResponse;
import project.camus.webmvc.common.constants.CamusConstants;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseWrapper {

    public static <T> ResponseEntity<SuccessResponse<T>> success(T data) {

        BodyBuilder builder = ResponseEntity.ok();
        addHeaders(builder, CamusConstants.TRACE_ID);

        return builder.body(CastUtils.cast(SuccessResponse.builder()
            .result(data)
            .timestamp(System.currentTimeMillis())
            .build()));
    }

    public static <T> ResponseEntity<SuccessResponse<T>> success() {

        BodyBuilder builder = ResponseEntity.ok();
        addHeaders(builder, CamusConstants.TRACE_ID);

        return builder.build();
    }

    private static void addHeaders(BodyBuilder builder, String... headerNames) {

        if (ArrayUtils.isEmpty(headerNames)) {
            return;
        }

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(requestAttributes)) {

            HttpServletRequest request = requestAttributes.getRequest();
            HttpHeaders headers = new HttpHeaders();
            Arrays.stream(headerNames).forEach(name -> {
                Object attribute = request.getAttribute(name);
                if (Objects.nonNull(attribute)) {
                    headers.add(name, attribute.toString());
                }
            });
            builder.headers(headers);
        }
    }
}
