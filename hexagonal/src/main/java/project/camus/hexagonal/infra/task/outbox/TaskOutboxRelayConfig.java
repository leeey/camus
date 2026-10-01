package project.camus.hexagonal.infra.task.outbox;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import project.camus.event.task.TaskEvent;
import project.camus.hexagonal.infra.kafka.KafkaTopicProperties;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(OutboxProperties.class)
@ConditionalOnProperty(value = "camus.outbox.relay.enabled", havingValue = "true", matchIfMissing = true)
public class TaskOutboxRelayConfig {

    @Bean
    public TaskOutboxRelay taskOutboxRelay(JdbcClient jdbcClient, KafkaTemplate<String, TaskEvent> kafkaTemplate,
        OutboxProperties outboxProperties, KafkaTopicProperties topicProperties) {

        return new TaskOutboxRelay(jdbcClient, kafkaTemplate, outboxProperties.relay(), topicProperties.taskEvents());
    }
}
