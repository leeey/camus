package project.kamus.infra.jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "project.kamus.infra.jpa.*")
public class InfraJpaApplication {

  public static void main(String[] args) {

    SpringApplication.run(InfraJpaApplication.class, args);
  }
}
