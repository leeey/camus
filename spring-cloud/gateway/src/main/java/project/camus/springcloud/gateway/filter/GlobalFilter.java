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

@Slf4j
@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter() {

        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        return new OrderedGatewayFilter((exchange, chain) -> {
            String traceId = UUID.randomUUID().toString();
            exchange.getRequest().mutate().header(Config.TRACE_ID, traceId).build();
            exchange.getResponse().getHeaders().add(Config.TRACE_ID, traceId);
            return chain.filter(exchange);
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
