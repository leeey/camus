package project.camus.springcloud.config.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * config server 기본 인증.
 * <ul>
 *     <li>CLIENT: 설정 조회 (gateway, example-api 등 config client)</li>
 *     <li>ADMIN: 설정 조회 + /encrypt, /decrypt, busrefresh</li>
 * </ul>
 */
@Configuration
@EnableConfigurationProperties(ConfigServerSecurityConfig.Accounts.class)
public class ConfigServerSecurityConfig {

    @Bean
    public SecurityFilterChain configServerSecurityFilterChain(HttpSecurity http) throws Exception {

        return http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/actuator/health/**").permitAll()
                .requestMatchers("/encrypt/**", "/decrypt/**", "/actuator/**").hasRole("ADMIN")
                .anyRequest().hasAnyRole("CLIENT", "ADMIN"))
            .httpBasic(Customizer.withDefaults())
            .build();
    }

    /**
     * 비밀번호는 환경변수(Secret)로 받아 메모리에만 둔다. 로컬 기본값은 운영에서 반드시 바꿔 주세요.
     */
    @Bean
    public UserDetailsService configServerUsers(Accounts accounts) {

        return new InMemoryUserDetailsManager(
            User.withUsername(accounts.clientUsername()).password("{noop}" + accounts.clientPassword()).roles("CLIENT").build(),
            User.withUsername(accounts.adminUsername()).password("{noop}" + accounts.adminPassword()).roles("ADMIN").build());
    }

    @ConfigurationProperties("camus.config.security")
    public record Accounts(String clientUsername, String clientPassword, String adminUsername, String adminPassword) {

    }
}
