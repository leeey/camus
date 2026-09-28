package project.camus.aws.client

import com.amazonaws.services.kms.AWSKMS
import com.amazonaws.services.kms.model.DecryptRequest
import com.amazonaws.services.kms.model.DecryptResult
import com.amazonaws.services.kms.model.EncryptRequest
import com.amazonaws.services.kms.model.EncryptResult
import com.amazonaws.services.kms.model.EncryptionAlgorithmSpec
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import project.camus.aws.client.builder.AwsKmsBuilder
import spock.lang.Requires
import spock.lang.Specification

class AwsKmsClientTest extends Specification {

    def kms = Mock(AWSKMS)
    def builder = Stub(AwsKmsBuilder) {
        build() >> kms
    }
    def client = new AwsKmsClient(builder)

    def "encrypt & decrypt"() {

        given:
        def word = "hello 안녕"
        def cipherBytes = "cipher".getBytes(StandardCharsets.UTF_8)

        when:
        def cipher = client.encrypt(word)

        then:
        1 * kms.encrypt({ EncryptRequest request ->
            request.keyId != null &&
                request.encryptionAlgorithm == EncryptionAlgorithmSpec.RSAES_OAEP_SHA_256.toString() &&
                StandardCharsets.UTF_8.decode(request.plaintext).toString() == word
        }) >> new EncryptResult().withCiphertextBlob(ByteBuffer.wrap(cipherBytes))
        cipher == Base64.encoder.encodeToString(cipherBytes)

        when:
        def plain = client.decrypt(cipher)

        then:
        1 * kms.decrypt({ DecryptRequest request ->
            request.ciphertextBlob == ByteBuffer.wrap(cipherBytes)
        }) >> new DecryptResult().withPlaintext(ByteBuffer.wrap(word.getBytes(StandardCharsets.UTF_8)))
        plain == word
    }

    @Requires({ env.AWS_KMS_IT == 'true' })
    def "encrypt & decrypt with real aws kms"() {

        given:
        def realClient = new AwsKmsClient(new AwsKmsBuilder())
        def word = "hello"

        expect:
        realClient.decrypt(realClient.encrypt(word)) == word
    }
}
