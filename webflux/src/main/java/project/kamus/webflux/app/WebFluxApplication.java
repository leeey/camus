package project.kamus.webflux.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootApplication(scanBasePackages = "project.kamus.webflux.*")
public class WebFluxApplication {

  public static void main(String[] args) {

    SpringApplication.run(WebFluxApplication.class, args);
  }
}
