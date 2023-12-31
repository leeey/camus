package project.camus.database.redis.config.openapi;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisOpenApiConfig {

    @Value("${server.port}")
    private String port;

    @Bean
    public GroupedOpenApi redisOpenApi() {

        return GroupedOpenApi.builder()
            .group("redis")
            .packagesToScan("project.camus.database.redis")
            .addOpenApiCustomizer(e -> e.info(new Info()
                    .title("Redis")
                    .version("1.0"))
                .servers(List.of(new Server().url("http://localhost:" + port))))
            .build();
    }
}
