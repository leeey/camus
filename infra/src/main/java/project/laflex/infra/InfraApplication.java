package project.laflex.infra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "project.laflex.infra.*")
public class InfraApplication {

  public static void main(String[] args) {

    SpringApplication.run(InfraApplication.class, args);
  }
}
