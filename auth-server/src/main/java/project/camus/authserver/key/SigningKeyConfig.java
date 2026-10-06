package project.camus.authserver.key;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.util.StringUtils;

/**
 * 서명 키(JWK) 구성. 공개키는 /oauth2/jwks 로 공개되어 resource server 가 토큰을 검증한다.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(SigningKeyProperties.class)
public class SigningKeyConfig {

    @Bean
    public JWKSource<SecurityContext> jwkSource(SigningKeyProperties properties) {

        return new ImmutableJWKSet<>(new JWKSet(rsaKey(properties)));
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {

        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    static RSAKey rsaKey(SigningKeyProperties properties) {

        String keyId = StringUtils.hasText(properties.keyId()) ? properties.keyId() : UUID.randomUUID().toString();
        if (!StringUtils.hasText(properties.privateKey())) {
            log.warn("camus.auth.signing-key.private-key 가 없어 임시 서명 키를 만듭니다. 재기동하거나 인스턴스가 여러 개면 "
                + "이전 토큰을 검증할 수 없으므로 로컬에서만 사용해 주세요.");
            KeyPair keyPair = generate();
            return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey(keyPair.getPrivate())
                .keyID(keyId)
                .build();
        }
        RSAPrivateCrtKey privateKey = parsePrivateKey(properties.privateKey());
        return new RSAKey.Builder(publicKeyOf(privateKey))
            .privateKey(privateKey)
            .keyID(keyId)
            .build();
    }

    private static RSAPrivateCrtKey parsePrivateKey(String pem) {

        String base64 = pem.replaceAll("-----(BEGIN|END) PRIVATE KEY-----", "").replaceAll("\\s", "");
        try {
            return (RSAPrivateCrtKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64)));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException | ClassCastException e) {
            throw new IllegalStateException("invalid PKCS#8 RSA private key (camus.auth.signing-key.private-key)", e);
        }
    }

    private static RSAPublicKey publicKeyOf(RSAPrivateCrtKey privateKey) {

        try {
            return (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent()));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException(e);
        }
    }

    private static KeyPair generate() {

        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
