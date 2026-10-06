package project.camus.hexagonal.infra.task.outbox;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.jdbc.core.simple.JdbcClient;

/**
 * outbox 지표.
 * <ul>
 *     <li>outbox.events.pending: 아직 발행하지 않은 이벤트 수 (계속 늘면 relay 또는 kafka 장애)</li>
 *     <li>outbox.events.published: 발행 결과 (result=success|failure)</li>
 * </ul>
 */
public class OutboxMetrics {

    private final Counter published;

    private final Counter failed;

    public OutboxMetrics(MeterRegistry registry, JdbcClient jdbcClient) {

        Gauge.builder("outbox.events.pending", jdbcClient, OutboxMetrics::countPending)
            .description("number of outbox events not yet published")
            .register(registry);
        this.published = Counter.builder("outbox.events.published")
            .description("outbox publish attempts")
            .tag("result", "success")
            .register(registry);
        this.failed = Counter.builder("outbox.events.published")
            .description("outbox publish attempts")
            .tag("result", "failure")
            .register(registry);
    }

    void published() {

        published.increment();
    }

    void failed() {

        failed.increment();
    }

    private static double countPending(JdbcClient jdbcClient) {

        return jdbcClient.sql("SELECT count(*) FROM outbox_event WHERE published_at IS NULL")
            .query(Long.class)
            .single();
    }
}
