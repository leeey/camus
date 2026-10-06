package project.camus.observability.task;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FeignTaskApplication {

    public static void main(String[] args) {

        SpringApplication.run(FeignTaskApplication.class, args);
    }

    // @Observed 가 동작하려면 aspect 가 필요하다.
    @Bean
    ObservedAspect observedAspect(ObservationRegistry observationRegistry) {

        return new ObservedAspect(observationRegistry);
    }
}
