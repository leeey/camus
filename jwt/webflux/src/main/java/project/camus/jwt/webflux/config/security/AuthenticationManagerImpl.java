package project.camus.jwt.webflux.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationManagerImpl implements ReactiveAuthenticationManager {

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        //TODO: find user data from database and authenticate it
        return Mono.just(new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), null,
            authentication.getAuthorities()));
    }
}
