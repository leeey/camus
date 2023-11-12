package project.camus.webflux.common.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authorization.HttpStatusServerAccessDeniedHandler;
import project.camus.webflux.common.config.security.jwt.JwtSecurityContextRepository;
import project.camus.webflux.common.config.security.jwt.JwtAuthenticationManager;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtSecurityContextRepository jwtSecurityContextRepository;

    private final JwtAuthenticationManager jwtAuthenticationManager;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
            .csrf(CsrfSpec::disable)
            .formLogin(FormLoginSpec::disable)
            .httpBasic(HttpBasicSpec::disable)
            .securityContextRepository(jwtSecurityContextRepository)
            .authenticationManager(jwtAuthenticationManager)
            .authorizeExchange(exchangeSpec -> exchangeSpec
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/favicon.ico").permitAll()
                .pathMatchers("/swagger-ui.html", "/webjars/swagger-ui/**", "/api-docs/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/healthcheck").permitAll()
                .pathMatchers("/lotto/**").permitAll()
                .pathMatchers(HttpMethod.POST, "/jwt/tokens").permitAll()
                .anyExchange().authenticated())
            .exceptionHandling(e ->
                e.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
                    .accessDeniedHandler(new HttpStatusServerAccessDeniedHandler(HttpStatus.FORBIDDEN)))
            .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/resources/**", "/static/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
