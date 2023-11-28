package project.camus.kafka.producer.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${server.port}")
    private String port;

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
            .components(new Components())
            .info(info());
    }

    private Info info() {

        return new Info().title("Kafka Producer API")
            .version("1.0")
            .description("Kafka Producer API");
    }
}
