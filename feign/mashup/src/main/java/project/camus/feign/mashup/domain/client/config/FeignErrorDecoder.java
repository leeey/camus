package project.camus.feign.mashup.domain.client.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import project.camus.common.exception.CamusServerException;

public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {

        return new CamusServerException(response.reason());
    }
}
