package project.camus.jwt.webmvc.config;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import project.camus.common.FailureResponse;
import project.camus.common.util.ObjectMapperUtil;
import project.camus.jwt.webmvc.config.security.JwtAuthorities;

@EnableWebSecurity
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Order(2)
    @Bean
    public SecurityFilterChain authFilterChain(HttpSecurity http) throws Exception {

        return http
            .cors(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .headers(configurer -> configurer.frameOptions(FrameOptionsConfig::sameOrigin))
            .authorizeHttpRequests(registry -> registry
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                .requestMatchers("/favicon.ico").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").permitAll()
                .requestMatchers("/actuator/health/**").permitAll()
                .anyRequest()
                .authenticated())
            // auth-server 가 발급한 JWT 를 검증하는 resource server. 토큰이 없거나 잘못되면 401, 권한이 없으면 403.
            .oauth2ResourceServer(resourceServer -> resourceServer
                .jwt(jwt -> jwt.jwtAuthenticationConverter(JwtAuthorities.converter()))
                .authenticationEntryPoint((request, response, exception) ->
                    toResponse(response, HttpStatus.UNAUTHORIZED.value(), "unauthorized"))
                .accessDeniedHandler((request, response, exception) ->
                    toResponse(response, HttpStatus.FORBIDDEN.value(), "forbidden")))
            .requestCache(RequestCacheConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
    }

    private static void toResponse(HttpServletResponse response, int httpStatus, String exception) throws IOException {

        response.setStatus(httpStatus);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(ObjectMapperUtil.toJson(FailureResponse.builder()
            .timestamp(System.currentTimeMillis())
            .errors(List.of(exception))
            .build()));
        writer.flush();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.OPTIONS.name())
            .allowedHeaders(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE);
    }
}
