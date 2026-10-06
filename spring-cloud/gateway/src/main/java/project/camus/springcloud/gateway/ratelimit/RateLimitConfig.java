package project.camus.springcloud.gateway.ratelimit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.support.ipresolver.XForwardedRemoteAddressResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    /**
     * rate limit key: 인증된 사용자는 사용자 이름(sub), 그 밖에는 클라이언트 IP.
     * <p>
     * X-Forwarded-For 는 클라이언트가 위조할 수 있으므로, 앞단의 신뢰할 수 있는 프록시(LB) 수만큼만 뒤에서부터 읽는다.
     */
    @Bean
    public KeyResolver clientIpKeyResolver(@Value("${camus.gateway.trusted-proxies:1}") int trustedProxies) {

        XForwardedRemoteAddressResolver resolver = XForwardedRemoteAddressResolver.maxTrustedIndex(trustedProxies);
        return exchange -> exchange.getPrincipal()
            .map(principal -> "user:" + principal.getName())
            .switchIfEmpty(Mono.fromSupplier(() -> "ip:" + resolver.resolve(exchange).getAddress().getHostAddress()));
    }
}
