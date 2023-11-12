package project.camus.hexagonal.web.common.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${server.port}")
    private String port;

    @Bean
    public GroupedOpenApi taskV1OpenApi() {

        return GroupedOpenApi.builder()
            .group("task - v1")
            .pathsToMatch("/v1/tasks/**")
            .addOpenApiCustomizer(e -> e.info(new Info()
                    .title("Task - V1")
                    .version("1.0"))
                .servers(List.of(new Server().url("http://127.0.0.1:" + port))))
            .build();
    }
}
