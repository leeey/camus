package project.camus.jwt.webflux.config.security;

import io.jsonwebtoken.Claims;
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

        Claims claims = jwtTokenProvider.getClaims(authentication.getPrincipal().toString());
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(claims.getSubject());
        return Mono.just(new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
            List.of(authority)));
    }
}
