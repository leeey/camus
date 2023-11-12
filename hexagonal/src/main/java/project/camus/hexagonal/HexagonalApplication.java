package project.camus.hexagonal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "project.camus.hexagonal.*",
    "project.camus.database.jpa.*"
})
public class HexagonalApplication {

    public static void main(String[] args) {

        SpringApplication.run(HexagonalApplication.class, args);
    }
}
