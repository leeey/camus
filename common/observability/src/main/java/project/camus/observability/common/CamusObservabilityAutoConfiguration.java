package project.camus.observability.common;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * 모든 서비스에 공통으로 적용하는 관측 설정.
 * <ul>
 *     <li>모든 지표에 application 태그</li>
 *     <li>http server/client 요청 지연 시간 histogram (p95, p99 계산용)</li>
 * </ul>
 */
@AutoConfiguration
public class CamusObservabilityAutoConfiguration {

    static final Set<String> HISTOGRAM_METERS = Set.of("http.server.requests", "http.client.requests");

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> camusApplicationTag(
        @Value("${spring.application.name:unknown}") String applicationName) {

        return registry -> registry.config().commonTags("application", applicationName);
    }

    @Bean
    public MeterFilter camusHttpHistogram() {

        return new MeterFilter() {

            @Override
            public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {

                if (HISTOGRAM_METERS.contains(id.getName())) {
                    return DistributionStatisticConfig.builder()
                        .percentilesHistogram(true)
                        .build()
                        .merge(config);
                }
                return config;
            }
        };
    }
}
