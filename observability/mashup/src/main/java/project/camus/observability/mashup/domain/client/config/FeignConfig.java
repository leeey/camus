package project.camus.observability.mashup.domain.client.config;

import feign.Logger;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// trace context(traceparent) 는 feign-micrometer 가 자동으로 전파한다.
@Configuration
public class FeignConfig {

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
