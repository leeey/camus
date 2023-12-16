package project.camus.jwt.webflux.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authorization.HttpStatusServerAccessDeniedHandler;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import project.camus.jwt.webflux.config.security.AuthenticationManagerImpl;
import project.camus.jwt.webflux.config.security.SecurityContextRepositoryImpl;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
@RequiredArgsConstructor
public class WebFluxConfig implements WebFluxConfigurer {

    private final SecurityContextRepositoryImpl securityContextRepository;

    private final AuthenticationManagerImpl authenticationManager;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
            .csrf(CsrfSpec::disable)
            .formLogin(FormLoginSpec::disable)
            .httpBasic(HttpBasicSpec::disable)
            .securityContextRepository(securityContextRepository)
            .authenticationManager(authenticationManager)
            .authorizeExchange(spec -> spec
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/favicon.ico").permitAll()
                .pathMatchers("/swagger-ui.html", "/webjars/swagger-ui/**", "/api-docs/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/healthcheck").permitAll()
                .pathMatchers(HttpMethod.POST, "/users/jwt").permitAll()
                .anyExchange().authenticated())
            .exceptionHandling(spec ->
                spec.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
                    .accessDeniedHandler(new HttpStatusServerAccessDeniedHandler(HttpStatus.FORBIDDEN)))
            .build();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("*")
            .allowedHeaders(HttpHeaders.AUTHORIZATION);
    }
}
