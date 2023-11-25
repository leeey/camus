package project.camus.springcloud.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication(scanBasePackages = {
    "project.camus.springcloud.config",
    "project.camus.aws.client",
})
public class SpringCloudConfigApplication {

    public static void main(String[] args) {

        SpringApplication.run(SpringCloudConfigApplication.class, args);
    }
}
