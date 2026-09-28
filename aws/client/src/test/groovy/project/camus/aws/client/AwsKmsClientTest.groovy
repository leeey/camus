package project.camus.aws.client

import java.nio.charset.StandardCharsets
import project.camus.aws.client.config.AwsProperties
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kms.KmsClient
import software.amazon.awssdk.services.kms.model.DecryptRequest
import software.amazon.awssdk.services.kms.model.DecryptResponse
import software.amazon.awssdk.services.kms.model.EncryptRequest
import software.amazon.awssdk.services.kms.model.EncryptResponse
import software.amazon.awssdk.services.kms.model.EncryptionAlgorithmSpec
import spock.lang.Requires
import spock.lang.Specification

class AwsKmsClientTest extends Specification {

    static final String KEY_ID = "test-key-id"

    def kms = Mock(KmsClient)
    def client = new AwsKmsClient(kms, new AwsProperties("ap-northeast-2", new AwsProperties.Kms(KEY_ID)))

    def "encrypt & decrypt"() {

        given:
        def word = "hello 안녕"
        def cipherBytes = "cipher".getBytes(StandardCharsets.UTF_8)

        when:
        def cipher = client.encrypt(word)

        then:
        1 * kms.encrypt({ EncryptRequest request ->
            request.keyId() == KEY_ID &&
                request.encryptionAlgorithm() == EncryptionAlgorithmSpec.RSAES_OAEP_SHA_256 &&
                request.plaintext().asUtf8String() == word
        }) >> EncryptResponse.builder().ciphertextBlob(SdkBytes.fromByteArray(cipherBytes)).build()
        cipher == Base64.encoder.encodeToString(cipherBytes)

        when:
        def plain = client.decrypt(cipher)

        then:
        1 * kms.decrypt({ DecryptRequest request ->
            request.keyId() == KEY_ID && request.ciphertextBlob().asByteArray() == cipherBytes
        }) >> DecryptResponse.builder().plaintext(SdkBytes.fromUtf8String(word)).build()
        plain == word
    }

    def "decrypt multi-line base64 cipher"() {

        given:
        def cipherBytes = new byte[100]
        new Random(1).nextBytes(cipherBytes)
        def multiLineCipher = Base64.encoder.encodeToString(cipherBytes).replaceAll(/(.{40})/, '$1\n')

        when:
        client.decrypt(multiLineCipher)

        then:
        1 * kms.decrypt({ DecryptRequest request -> request.ciphertextBlob().asByteArray() == cipherBytes }) >>
            DecryptResponse.builder().plaintext(SdkBytes.fromUtf8String("plain")).build()
    }

    def "key-id is required"() {

        given:
        def noKeyClient = new AwsKmsClient(kms, new AwsProperties("ap-northeast-2", new AwsProperties.Kms(null)))

        when:
        noKeyClient.encrypt("hello")

        then:
        thrown(IllegalArgumentException)
        0 * kms._
    }

    @Requires({ env.AWS_KMS_IT == 'true' })
    def "encrypt & decrypt with real aws kms"() {

        given:
        def keyId = System.getenv("AWS_KMS_KEY_ID") ?: "f096a65c-38d6-4a2b-b0c5-7f5c81636367"
        def kmsClient = KmsClient.builder()
            .credentialsProvider(DefaultCredentialsProvider.builder().build())
            .region(Region.AP_NORTHEAST_2)
            .build()
        def realClient = new AwsKmsClient(kmsClient, new AwsProperties("ap-northeast-2", new AwsProperties.Kms(keyId)))

        expect:
        realClient.decrypt(realClient.encrypt("hello")) == "hello"

        cleanup:
        kmsClient.close()
    }
}
