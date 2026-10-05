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

    // 재시도는 resilience4j retry 가 담당한다. feign 자체 재시도와 겹치면 재시도 횟수가 곱해진다.
    @Bean
    Retryer retryer() {

        return Retryer.NEVER_RETRY;
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
