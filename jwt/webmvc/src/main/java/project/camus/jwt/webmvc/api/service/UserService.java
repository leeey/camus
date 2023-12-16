package project.camus.jwt.webmvc.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import project.camus.jwt.webmvc.api.dto.request.CreateJwtRequest;
import project.camus.jwt.webmvc.config.security.JwtProvider;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtProvider jwtProvider;

    public String getJwt(CreateJwtRequest request) {

        request.validate();
        return jwtProvider.createToken(request.getUsername());
    }

    public String getUsername() {

        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
}
