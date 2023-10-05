package project.camus.infra.r2dbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootApplication(scanBasePackages = "project.camus.infra.r2dbc.*")
public class InfraR2dbcApplication {

  public static void main(String[] args) {

    SpringApplication.run(InfraR2dbcApplication.class, args);
  }
}
