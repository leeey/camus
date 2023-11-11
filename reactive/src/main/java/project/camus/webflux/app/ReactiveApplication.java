package project.camus.webflux.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootApplication(scanBasePackages = "project.camus.webflux.*")
public class ReactiveApplication {

    public static void main(String[] args) {

        SpringApplication.run(ReactiveApplication.class, args);
    }
}
