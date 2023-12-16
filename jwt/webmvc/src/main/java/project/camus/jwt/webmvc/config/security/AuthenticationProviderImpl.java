package project.camus.jwt.webmvc.config.security;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationProviderImpl implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) {

        //TODO: find user data from database and authenticate it
        return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(),
            authentication.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return Objects.equals(UsernamePasswordAuthenticationToken.class, authentication);
    }
}
