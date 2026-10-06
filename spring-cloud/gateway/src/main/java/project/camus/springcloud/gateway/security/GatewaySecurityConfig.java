package project.camus.springcloud.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * gateway 에서 auth-server 의 JWT 를 검증한다 (edge 인증).
 * <ul>
 *     <li>task-service: 조회는 task.read, 변경은 task.write scope 가 필요하다. 토큰이 없거나 잘못되면 401, scope 가 부족하면 403.</li>
 *     <li>검증한 토큰(Authorization 헤더)은 그대로 하위 서비스로 전달한다.</li>
 * </ul>
 */
@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    private static final String TASK_SERVICE = "/task-service/**";

    @Bean
    public SecurityWebFilterChain gatewaySecurityFilterChain(ServerHttpSecurity http) {

        return http
            .csrf(CsrfSpec::disable)
            .formLogin(FormLoginSpec::disable)
            .httpBasic(HttpBasicSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers(HttpMethod.GET, TASK_SERVICE).hasAuthority("SCOPE_task.read")
                .pathMatchers(TASK_SERVICE).hasAuthority("SCOPE_task.write")
                .anyExchange().permitAll())
            .oauth2ResourceServer(resourceServer -> resourceServer.jwt(jwt -> { }))
            .build();
    }
}
