package project.camus.springcloud.exampleapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SpringCloudExampleApiApplication {

    public static void main(String[] args) {

        SpringApplication.run(SpringCloudExampleApiApplication.class, args);
    }
}
