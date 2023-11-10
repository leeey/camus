package project.camus.orm.jpa.config;

import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import project.camus.orm.jpa.model.JpaModel;

@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = "jpaEntityAuditAware")
@EnableJpaRepositories(basePackageClasses = {JpaModel.class})
@EntityScan(basePackageClasses = {JpaModel.class})
@Configuration
public class JpaConfig {

}
