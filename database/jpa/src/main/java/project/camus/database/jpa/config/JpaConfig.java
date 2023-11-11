package project.camus.database.jpa.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import project.camus.database.jpa.model.JpaModel;

@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = "jpaEntityAuditAware")
@EnableJpaRepositories(basePackageClasses = {JpaModel.class})
@EntityScan(basePackageClasses = {JpaModel.class})
@Configuration
public class JpaConfig {

}
