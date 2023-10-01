package project.laflex.lotto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "project.laflex.*")
public class LottoApplication {

  public static void main(String[] args) {

    SpringApplication.run(LottoApplication.class, args);
  }
}
