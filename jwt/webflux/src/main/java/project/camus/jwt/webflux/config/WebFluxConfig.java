package project.camus.jwt.webflux.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import project.camus.jwt.webflux.config.security.JwtAuthorities;

/**
 * auth-server 가 발급한 JWT 를 검증하는 resource server (webflux).
 */
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
            .csrf(CsrfSpec::disable)
            .formLogin(FormLoginSpec::disable)
            .httpBasic(HttpBasicSpec::disable)
            .authorizeExchange(spec -> spec
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/favicon.ico").permitAll()
                .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").permitAll()
                .pathMatchers("/actuator/health/**").permitAll()
                .anyExchange().authenticated())
            .oauth2ResourceServer(resourceServer -> resourceServer
                .jwt(jwt -> jwt.jwtAuthenticationConverter(
                    new ReactiveJwtAuthenticationConverterAdapter(JwtAuthorities.converter()))))
            .build();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("*")
            .allowedHeaders(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE);
    }
}
