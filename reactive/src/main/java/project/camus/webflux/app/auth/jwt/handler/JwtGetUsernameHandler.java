package project.camus.webflux.app.auth.jwt.handler;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import project.camus.common.exception.CamusServerException;
import project.camus.webflux.config.ResponseWrapper;
import project.camus.webflux.config.security.jwt.JwtTokenProvider;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtGetUsernameHandler implements HandlerFunction<ServerResponse> {

    private final JwtTokenProvider jwtTokenProvider;

    @NonNull
    @Override
    public Mono<ServerResponse> handle(@NonNull ServerRequest request) {

        Optional<String> headerOptional = Optional.ofNullable(request.headers().firstHeader(HttpHeaders.AUTHORIZATION));

        return headerOptional.map(s -> ResponseWrapper.success(jwtTokenProvider.getUsername(s.substring(7))))
            .orElseGet(() -> Mono.error(new CamusServerException(new InternalAuthenticationServiceException(HttpHeaders.AUTHORIZATION))));
    }
}
