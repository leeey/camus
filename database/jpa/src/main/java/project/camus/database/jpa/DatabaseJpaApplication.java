package project.camus.database.jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "project.camus.database.jpa.*"
})
public class DatabaseJpaApplication {

    public static void main(String[] args) {

        SpringApplication.run(DatabaseJpaApplication.class, args);
    }
}
