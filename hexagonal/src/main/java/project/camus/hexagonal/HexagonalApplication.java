package project.camus.hexagonal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication(scanBasePackages = {
    "project.camus.hexagonal.*",
    "project.camus.orm.jpa.*"
})
public class HexagonalApplication {

    public static void main(String[] args) {

        SpringApplication.run(HexagonalApplication.class, args);
    }
}
