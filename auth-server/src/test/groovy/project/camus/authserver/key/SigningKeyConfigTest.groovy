package project.camus.authserver.key

import java.security.KeyPairGenerator
import java.security.interfaces.RSAPublicKey
import spock.lang.Specification

class SigningKeyConfigTest extends Specification {

    def "loads the signing key from a PKCS#8 PEM"() {

        given:
        def keyPair = KeyPairGenerator.getInstance("RSA").with { initialize(2048); generateKeyPair() }
        def pem = "-----BEGIN PRIVATE KEY-----\n" +
            Base64.getMimeEncoder(64, "\n".bytes).encodeToString(keyPair.private.encoded) +
            "\n-----END PRIVATE KEY-----\n"

        when:
        def key = SigningKeyConfig.rsaKey(new SigningKeyProperties("key-2026-10", pem))

        then:
        key.keyID == "key-2026-10"
        key.toRSAPublicKey().modulus == (keyPair.public as RSAPublicKey).modulus
        key.isPrivate()
    }

    def "fails fast with an invalid PEM"() {

        when:
        SigningKeyConfig.rsaKey(new SigningKeyProperties("kid", "not a key"))

        then:
        thrown(IllegalStateException)
    }

    def "generates a temporary key when no PEM is configured"() {

        when:
        def key = SigningKeyConfig.rsaKey(new SigningKeyProperties("kid", ""))

        then:
        key.keyID == "kid"
        key.isPrivate()
    }
}
