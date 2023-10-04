package project.kamus.infra.jpa.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import project.kamus.infra.jpa.model.JpaModel;

@EnableJpaAuditing(auditorAwareRef = "jpaEntityAuditAware")
@EnableJpaRepositories(basePackageClasses = {JpaModel.class})
@EntityScan(basePackageClasses = {JpaModel.class})
@Configuration
public class JpaConfig {

}
