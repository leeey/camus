package project.camus.springcloud.config.common;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.config.server.environment.JGitEnvironmentProperties;
import org.springframework.cloud.config.server.environment.JGitEnvironmentRepository;
import org.springframework.cloud.config.server.environment.MultipleJGitEnvironmentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import project.camus.aws.service.AwsKmsService;

@Configuration
public class ConfigServerConfig {

    private final ConfigurableEnvironment configurableEnvironment;

    private final JGitEnvironmentProperties jGitEnvironmentProperties;

    private final ObservationRegistry observationRegistry;

    private final String gitPassword;

    private final AwsKmsService awsKmsService;

    public ConfigServerConfig(ConfigurableEnvironment configurableEnvironment, MultipleJGitEnvironmentProperties jGitEnvironmentProperties,
        ObservationRegistry observationRegistry, @Value("${git.password}") String gitPassword,
        AwsKmsService awsKmsService) {

        this.configurableEnvironment = configurableEnvironment;
        this.jGitEnvironmentProperties = jGitEnvironmentProperties;
        this.observationRegistry = observationRegistry;
        this.gitPassword = gitPassword;
        this.awsKmsService = awsKmsService;
    }

    @Bean
    public JGitEnvironmentRepository jGitEnvironmentRepository() {

        jGitEnvironmentProperties.setUri("https://github.com/leeey/camus-config-resources.git");
        jGitEnvironmentProperties.setUsername("leeey");
        jGitEnvironmentProperties.setPassword(awsKmsService.decrypt(gitPassword));
        jGitEnvironmentProperties.setDefaultLabel("main");
        jGitEnvironmentProperties.setSearchPaths("config", "gateway", "api");

        return new JGitEnvironmentRepository(configurableEnvironment, jGitEnvironmentProperties, observationRegistry);
    }
}
