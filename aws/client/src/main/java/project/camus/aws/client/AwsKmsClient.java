package project.camus.aws.client;

import java.util.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import project.camus.aws.client.config.AwsProperties;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptionAlgorithmSpec;

@Component
public class AwsKmsClient {

    private final KmsClient kmsClient;

    private final String keyId;

    public AwsKmsClient(KmsClient kmsClient, AwsProperties properties) {

        this.kmsClient = kmsClient;
        this.keyId = properties.kms().keyId();
    }

    public String encrypt(String plain) {

        Assert.hasText(keyId, "camus.aws.kms.key-id is required");

        EncryptRequest request = EncryptRequest.builder()
            .keyId(keyId)
            .plaintext(SdkBytes.fromUtf8String(plain))
            .encryptionAlgorithm(EncryptionAlgorithmSpec.RSAES_OAEP_SHA_256)
            .build();

        return Base64.getEncoder().encodeToString(kmsClient.encrypt(request).ciphertextBlob().asByteArray());
    }

    public String decrypt(String cipher) {

        Assert.hasText(keyId, "camus.aws.kms.key-id is required");

        DecryptRequest request = DecryptRequest.builder()
            .keyId(keyId)
            .ciphertextBlob(SdkBytes.fromByteArray(Base64.getMimeDecoder().decode(cipher)))
            .encryptionAlgorithm(EncryptionAlgorithmSpec.RSAES_OAEP_SHA_256)
            .build();

        return kmsClient.decrypt(request).plaintext().asUtf8String();
    }
}
