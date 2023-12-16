package project.camus.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootApplication(scanBasePackages = "project.camus.webflux.*")
public class WebFluxApplication {

    public static void main(String[] args) {

        SpringApplication.run(WebFluxApplication.class, args);
    }
}
