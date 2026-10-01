package project.camus.hexagonal.infra.task.outbox;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("camus.outbox")
public record OutboxProperties(@DefaultValue Relay relay) {

    public record Relay(
        @DefaultValue("true") boolean enabled,
        @DefaultValue("1s") Duration fixedDelay,
        @DefaultValue("100") int batchSize,
        @DefaultValue("10s") Duration sendTimeout
    ) {

    }
}
