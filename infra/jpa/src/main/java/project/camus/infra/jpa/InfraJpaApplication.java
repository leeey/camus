package project.camus.infra.jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "project.camus.infra.jpa.*")
public class InfraJpaApplication {

  public static void main(String[] args) {

    SpringApplication.run(InfraJpaApplication.class, args);
  }
}
