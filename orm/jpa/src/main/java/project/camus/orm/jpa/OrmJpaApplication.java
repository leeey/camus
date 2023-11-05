package project.camus.orm.jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "project.camus.orm.jpa.*")
public class OrmJpaApplication {

  public static void main(String[] args) {

    SpringApplication.run(OrmJpaApplication.class, args);
  }
}
