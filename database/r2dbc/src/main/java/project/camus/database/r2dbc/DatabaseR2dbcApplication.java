package project.camus.database.r2dbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootApplication(scanBasePackages = {
    "project.camus.database.r2dbc.*"
})
public class DatabaseR2dbcApplication {

    public static void main(String[] args) {

        SpringApplication.run(DatabaseR2dbcApplication.class, args);
    }
}
