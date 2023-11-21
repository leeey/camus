package project.camus.feign.mashup;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@Slf4j
@EnableFeignClients
@SpringBootApplication
public class FeignMashupApplication {

    public static void main(String[] args) {

        SpringApplication.run(FeignMashupApplication.class, args);
    }

    @Bean
    ObservedAspect observedAspect(ObservationRegistry observationRegistry) {

        return new ObservedAspect(observationRegistry);
    }
}
