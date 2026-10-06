package project.camus.springcloud.gateway.filter;

import io.micrometer.tracing.handler.TracingObservationHandler.TracingContext;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.observation.ServerRequestObservationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * 응답 헤더 Trace-Id 에 현재 요청의 traceId 를 담는다.
 * <p>
 * 하위 서비스로는 W3C traceparent 헤더로 trace context 가 전파되므로, 같은 traceId 로 모든 서비스의 trace 와 로그를 찾을 수 있다.
 */
@Slf4j
@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter() {

        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        return new OrderedGatewayFilter((exchange, chain) -> {
            traceId(exchange).ifPresent(traceId -> exchange.getResponse().getHeaders().set(Config.TRACE_ID, traceId));
            return chain.filter(exchange);
        }, Ordered.HIGHEST_PRECEDENCE);
    }

    private Optional<String> traceId(ServerWebExchange exchange) {

        return ServerRequestObservationContext.findCurrent(exchange.getAttributes())
            .map(context -> context.<TracingContext>get(TracingContext.class))
            .map(TracingContext::getSpan)
            .map(span -> span.context().traceId());
    }

    @Setter
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Config {

        public static final String TRACE_ID = "Trace-Id";

        private String filterName;
    }
}
