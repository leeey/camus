package project.camus.observability.mashup.domain.client.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import project.camus.common.exception.CamusClientException;

/**
 * 5xx 는 재시도할 수 있는 장애(DownstreamUnavailableException), 4xx 는 요청 오류(CamusClientException)로 구분한다.
 */
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {

        if (response.status() >= 500) {
            return new DownstreamUnavailableException(methodKey, response.status());
        }
        return new CamusClientException(new IllegalArgumentException(
            "downstream rejected the request. method=" + methodKey + ", status=" + response.status()));
    }
}
