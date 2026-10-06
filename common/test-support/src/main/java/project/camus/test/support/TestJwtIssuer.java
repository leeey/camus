package project.camus.test.support;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * 테스트용 토큰 발급기. auth-server 대신 RSA 키를 만들어 JWKS 를 제공하고 RS256 JWT 를 서명한다.
 */
public final class TestJwtIssuer {

    public static final String ISSUER = "http://test-issuer";

    private final RSAKey key;

    public TestJwtIssuer() {

        this("test-key");
    }

    public TestJwtIssuer(String keyId) {

        try {
            this.key = new RSAKeyGenerator(2048).keyID(keyId).generate();
        } catch (JOSEException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * /oauth2/jwks 응답 (공개키만)
     */
    public String jwksJson() {

        return new JWKSet(key.toPublicJWK()).toString();
    }

    public String token(String subject, List<String> scopes, List<String> roles) {

        return sign(claims(subject, scopes, roles, ISSUER, Instant.now().plus(Duration.ofMinutes(5))));
    }

    public String token(String subject, List<String> scopes) {

        return token(subject, scopes, List.of());
    }

    public String expiredToken(String subject, List<String> scopes) {

        return sign(claims(subject, scopes, List.of(), ISSUER, Instant.now().minus(Duration.ofMinutes(5))));
    }

    public String tokenWithIssuer(String subject, List<String> scopes, String issuer) {

        return sign(claims(subject, scopes, List.of(), issuer, Instant.now().plus(Duration.ofMinutes(5))));
    }

    private static JWTClaimsSet claims(String subject, List<String> scopes, List<String> roles, String issuer,
        Instant expiresAt) {

        Instant now = Instant.now();
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
            .subject(subject)
            .issuer(issuer)
            .issueTime(Date.from(now.minusSeconds(60)))
            .notBeforeTime(Date.from(now.minusSeconds(60)))
            .expirationTime(Date.from(expiresAt))
            .claim("scope", scopes);
        if (!roles.isEmpty()) {
            builder.claim("roles", roles);
        }
        return builder.build();
    }

    private String sign(JWTClaimsSet claims) {

        try {
            SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(key.getKeyID()).build(), claims);
            jwt.sign(new RSASSASigner(key));
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException(e);
        }
    }
}
