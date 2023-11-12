package project.camus.webflux.common.config.security.jwt;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        String authToken = authentication.getCredentials().toString();
        jwtTokenProvider.validate(authToken);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(jwtTokenProvider.getUsername(authToken));
        return Mono.just(new UsernamePasswordAuthenticationToken(jwtTokenProvider.getUsername(authToken), null,
            List.of(authority)));
    }
}
