package project.camus.hexagonal.infra.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("camus.kafka.topics")
public record KafkaTopicProperties(@DefaultValue Topic taskEvents) {

    public record Topic(
        @DefaultValue("task-events") String name,
        @DefaultValue("3") int partitions,
        @DefaultValue("3") short replicas,
        @DefaultValue("2") int minInsyncReplicas
    ) {

    }
}
