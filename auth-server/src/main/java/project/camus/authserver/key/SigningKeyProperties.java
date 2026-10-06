package project.camus.authserver.key;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * access token 서명 키 (RS256).
 *
 * @param keyId      JWK kid. 키를 교체할 때 바꾼다.
 * @param privateKey PKCS#8 PEM RSA private key. 비어 있으면 기동할 때마다 새 키를 만든다 (로컬 전용).
 */
@ConfigurationProperties("camus.auth.signing-key")
public record SigningKeyProperties(String keyId, String privateKey) {

}
