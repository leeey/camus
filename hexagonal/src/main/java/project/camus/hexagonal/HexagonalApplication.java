package project.camus.hexagonal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {
    "project.camus.hexagonal.*",
    "project.camus.database.jpa.*"
})
public class HexagonalApplication {

    public static void main(String[] args) {

        SpringApplication.run(HexagonalApplication.class, args);
    }
}
