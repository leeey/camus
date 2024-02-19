package project.camus.database.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "project.camus.database.graphql"
})
public class DatabaseGraphQLApplication {

    public static void main(String[] args) {

        SpringApplication.run(DatabaseGraphQLApplication.class, args);
    }
}
