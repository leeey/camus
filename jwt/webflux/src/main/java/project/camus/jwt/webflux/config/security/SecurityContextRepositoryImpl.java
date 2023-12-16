package project.camus.jwt.webflux.config.security;

import io.jsonwebtoken.Claims;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import project.camus.common.exception.CamusServerException;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class SecurityContextRepositoryImpl implements ServerSecurityContextRepository {

    private final AuthenticationManagerImpl jwtAuthenticationManager;

    private final JwtProvider jwtProvider;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {

        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {

        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
            .filter(authHeader -> authHeader.startsWith("Bearer "))
            .map(authHeader -> authHeader.substring(7))
            .flatMap(token -> {
                Claims claims = jwtProvider.getClaims(token);
                return jwtAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(claims.getSubject(), null, List.of()))
                    .map(auth -> {
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        return SecurityContextHolder.getContext();
                    });
            })
            .onErrorMap(CamusServerException::new);
    }

}
