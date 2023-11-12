package project.camus.webflux.common.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import project.camus.common.exception.CamusClientException;

@Slf4j
@Service
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

        return getPayload(token).getSubject();
    }

    public Claims getPayload(String token) {

        return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(tokenSecretBytes))
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public void validate(String token) {

        try {
            Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(tokenSecretBytes))
                .build()
                .parseSignedClaims(token);
        } catch (SignatureException | IllegalArgumentException | UnsupportedJwtException | ExpiredJwtException | MalformedJwtException ex) {
            throw new CamusClientException(ex);
        }
    }
}
