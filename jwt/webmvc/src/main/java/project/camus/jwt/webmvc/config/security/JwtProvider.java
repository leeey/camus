package project.camus.jwt.webmvc.config.security;

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
public class JwtProvider {

    private final byte[] tokenSecretBytes;

    private final long expirationMs;

    public JwtProvider(@Value("${jwt.token-secret}") String tokenSecret,
        @Value("${jwt.expiration-ms}") long expirationMs) {

        this.tokenSecretBytes = Base64.getEncoder().encode(tokenSecret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String createToken(String userId) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
            .subject(userId)
            .issuedAt(new Date())
            .expiration(expiryDate)
            .signWith(Keys.hmacShaKeyFor(tokenSecretBytes))
            .compact();
    }

    public Claims getClaims(String token) {

        return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(tokenSecretBytes))
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
