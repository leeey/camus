package project.camus.hexagonal.infra.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableConfigurationProperties(KafkaTopicProperties.class)
public class KafkaTopicConfig {

    @Bean
    public NewTopic taskEventsTopic(KafkaTopicProperties properties) {

        KafkaTopicProperties.Topic topic = properties.taskEvents();
        return TopicBuilder.name(topic.name())
            .partitions(topic.partitions())
            .replicas(topic.replicas())
            .config(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, String.valueOf(topic.minInsyncReplicas()))
            .build();
    }
}
