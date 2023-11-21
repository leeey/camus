package project.camus.feign.member;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FeignMemberApplication {

    public static void main(String[] args) {

        SpringApplication.run(FeignMemberApplication.class, args);
    }

    @Bean
    ObservedAspect observedAspect(ObservationRegistry observationRegistry) {

        return new ObservedAspect(observationRegistry);
    }
}
