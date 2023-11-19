package project.camus.feign.mashup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class FeignMashupApplication {

    public static void main(String[] args) {

        SpringApplication.run(FeignMashupApplication.class, args);
    }
}
