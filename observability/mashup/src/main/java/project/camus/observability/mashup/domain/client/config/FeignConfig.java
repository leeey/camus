package project.camus.observability.mashup.domain.client.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import java.util.Objects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import project.camus.webmvc.common.constants.CamusConstants;

@Configuration
public class FeignConfig {

    @Bean
    RequestInterceptor requestInterceptor() {

        return requestTemplate -> requestTemplate.header(CamusConstants.TRACE_ID,
            Objects.requireNonNull(Objects.requireNonNull(RequestContextHolder.getRequestAttributes())
                .getAttribute(CamusConstants.TRACE_ID, RequestAttributes.SCOPE_REQUEST)).toString());
    }

    @Bean
    Retryer retryer() {

        return new Retryer.Default();
    }

    @Bean
    ErrorDecoder errorDecoder() {

        return new FeignErrorDecoder();
    }

    @Bean
    Logger.Level feignLoggerLevel() {

        return Logger.Level.BASIC;
    }
}
