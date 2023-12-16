package project.camus.jwt.webflux.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    private final byte[] tokenSecretBytes;

    private final long expirationMs;

    public JwtTokenProvider(@Value("${jwt.tokenSecret}") String tokenSecret,
        @Value("${jwt.expirationMs}") long expirationMs) {

        this.tokenSecretBytes = Base64.getEncoder().encode(tokenSecret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String createToken(String username) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
            .subject(username)
            .issuedAt(new Date())
            .expiration(expiryDate)
            .signWith(Keys.hmacShaKeyFor(tokenSecretBytes))
            .compact();
    }

    public String getUsername(String token) {

        return getClaims(token).getSubject();
    }

    public Claims getClaims(String token) {

        return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(tokenSecretBytes))
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
