package project.camus.jwt.webflux.api.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import project.camus.common.util.ValidationUtil;
import project.camus.jwt.webflux.api.dto.request.CreateJwtRequestDto;
import project.camus.jwt.webflux.config.security.JwtProvider;
import project.camus.webflux.common.ResponseWrapper;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateJwtHandler implements HandlerFunction<ServerResponse> {

    private final JwtProvider jwtProvider;

    @NonNull
    @Override
    public Mono<ServerResponse> handle(@NonNull ServerRequest request) {

        return request.bodyToMono(CreateJwtRequestDto.class)
            .doOnNext(ValidationUtil::validate)
            .doOnNext(CreateJwtRequestDto::validate)
            .flatMap(dto -> {
                String token = jwtProvider.createToken(dto.getUsername());
                return ResponseWrapper.success(token);
            });
    }
}
