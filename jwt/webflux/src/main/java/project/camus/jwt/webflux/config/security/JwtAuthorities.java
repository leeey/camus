package project.camus.jwt.webflux.config.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

/**
 * JWT 의 scope 클레임은 SCOPE_xxx, roles 클레임은 ROLE_xxx 권한으로 바꾼다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtAuthorities {

    public static JwtAuthenticationConverter converter() {

        JwtGrantedAuthoritiesConverter scopes = new JwtGrantedAuthoritiesConverter();
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> Stream.concat(
                scopes.convert(jwt).stream(),
                roles(jwt).stream())
            .toList());
        return converter;
    }

    private static Collection<GrantedAuthority> roles(Jwt jwt) {

        List<String> roles = jwt.getClaimAsStringList("roles");
        if (roles == null) {
            return List.of();
        }
        return roles.stream().<GrantedAuthority>map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();
    }
}
