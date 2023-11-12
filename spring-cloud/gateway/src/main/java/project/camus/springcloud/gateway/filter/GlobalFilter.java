package project.camus.springcloud.gateway.filter;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter() {

        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        return new OrderedGatewayFilter((exchange, chain) -> {
            log.info(config.filterName + " started.");
            String traceId = UUID.randomUUID().toString();
            log.info("request trace-id: {}", traceId);
            exchange.getRequest().mutate().header(Config.TRACE_ID, traceId).build();

            return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> log.info("response trace-id: {}",
                    exchange.getResponse().getHeaders().getFirst(Config.TRACE_ID))));
        }, Ordered.HIGHEST_PRECEDENCE);
    }

    @Setter
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Config {

        public static final String TRACE_ID = "Trace-Id";

        private String filterName;
    }
}
