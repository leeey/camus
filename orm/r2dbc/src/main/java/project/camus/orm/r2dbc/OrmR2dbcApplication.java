package project.camus.orm.r2dbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootApplication(scanBasePackages = "project.camus.orm.r2dbc.*")
public class OrmR2dbcApplication {

  public static void main(String[] args) {

    SpringApplication.run(OrmR2dbcApplication.class, args);
  }
}
