package project.camus.jwt.webflux.api.handler;

import java.util.Map;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import project.camus.webflux.common.ResponseWrapper;
import reactor.core.publisher.Mono;

/**
 * 토큰의 사용자(sub)와 권한을 돌려준다.
 */
@Component
public class GetMeHandler implements HandlerFunction<ServerResponse> {

    @NonNull
    @Override
    public Mono<ServerResponse> handle(@NonNull ServerRequest request) {

        return ReactiveSecurityContextHolder.getContext()
            .map(context -> (JwtAuthenticationToken) context.getAuthentication())
            .map(authentication -> Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).sorted().toList()))
            .flatMap(ResponseWrapper::success);
    }
}
