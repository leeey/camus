package project.laflex.lotto.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LottoOpenApiConfig {

  @Bean
  public GroupedOpenApi lottoOpenApi() {

    return GroupedOpenApi.builder()
        .group("lotto")
        .packagesToScan("project.laflex.lotto")
        .addOpenApiCustomizer(e -> e.info(new Info()
                .title("Lotto 프로젝트")
                .version("1.0"))
            .servers(List.of(new Server().url("http://localhost:8080"))))
        .build();
  }
}
