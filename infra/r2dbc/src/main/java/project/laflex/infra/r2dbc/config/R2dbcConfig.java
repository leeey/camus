package project.laflex.infra.r2dbc.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import project.laflex.infra.r2dbc.model.R2dbcModel;

@EnableR2dbcAuditing(auditorAwareRef = "r2dbcEntityAuditAware")
@EnableR2dbcRepositories(basePackageClasses = {R2dbcModel.class})
@EntityScan(basePackageClasses = {R2dbcModel.class})
@Configuration
public class R2dbcConfig {

}
