package project.camus.springcloud.servicediscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication(scanBasePackages = {
    "project.camus.springcloud.servicediscovery"
})
public class SpringCloudServiceDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudServiceDiscoveryApplication.class, args);
    }
}
