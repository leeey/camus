package project.laflex.infra.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing(auditorAwareRef = "jpaEntityAuditAware")
@EnableJpaRepositories(basePackageClasses = {JpaModel.class})
@EntityScan(basePackageClasses = {JpaModel.class})
public class JpaConfig {
}
