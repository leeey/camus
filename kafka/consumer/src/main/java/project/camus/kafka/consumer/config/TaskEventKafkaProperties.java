package project.camus.kafka.consumer.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("camus.kafka.task-events")
public record TaskEventKafkaProperties(
    @DefaultValue("task-events") String topic,
    @DefaultValue("camus-task-view") String groupId,
    @DefaultValue("3") int concurrency,
    @DefaultValue("3") int dltPartitions,
    @DefaultValue("3") short dltReplicas,
    @DefaultValue Retry retry
) {

    public String dltTopic() {

        return topic + ".DLT";
    }

    public record Retry(
        @DefaultValue("3") int maxRetries,
        @DefaultValue("1s") Duration initialInterval,
        @DefaultValue("2.0") double multiplier
    ) {

    }
}
