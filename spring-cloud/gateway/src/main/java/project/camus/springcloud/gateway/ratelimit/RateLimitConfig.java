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
     * 클라이언트 IP 기준 rate limit key.
     * <p>
     * X-Forwarded-For 는 클라이언트가 위조할 수 있으므로, 앞단의 신뢰할 수 있는 프록시(LB) 수만큼만 뒤에서부터 읽는다.
     */
    @Bean
    public KeyResolver clientIpKeyResolver(@Value("${camus.gateway.trusted-proxies:1}") int trustedProxies) {

        XForwardedRemoteAddressResolver resolver = XForwardedRemoteAddressResolver.maxTrustedIndex(trustedProxies);
        return exchange -> Mono.just(resolver.resolve(exchange).getAddress().getHostAddress());
    }
}
