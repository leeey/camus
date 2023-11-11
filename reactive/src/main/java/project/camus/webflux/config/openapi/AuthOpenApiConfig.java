package project.camus.webflux.config.openapi;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class AuthOpenApiConfig {

    @Value("${server.port}")
    private String port;

    @Bean
    public GroupedOpenApi authOpenApi() {

        return GroupedOpenApi.builder()
            .group("auth-test")
            .packagesToScan("project.camus.webflux.app.auth")
            .addOpenApiCustomizer(e -> e.info(new Info()
                    .title("Auth Test")
                    .version("1.0"))
                .servers(List.of(new Server().url("http://localhost:" + port)))
                .schemaRequirement(HttpHeaders.AUTHORIZATION,
                    new SecurityScheme().in(In.HEADER).name(HttpHeaders.AUTHORIZATION).type(Type.APIKEY))
                .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION))
            )
            .build();
    }
}
