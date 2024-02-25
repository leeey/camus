package project.camus.aws.client;

import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.EncryptionAlgorithmSpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;
import project.camus.aws.client.builder.AwsKmsBuilder;

@Component
@RequiredArgsConstructor
public class AwsKmsClient {

    // this arn is only for test.
    private static final String KEY_ID = "f096a65c-38d6-4a2b-b0c5-7f5c81636367";

    private final AwsKmsBuilder awsKmsBuilder;

    public String encrypt(String plain) {

        EncryptRequest request = new EncryptRequest();
        request.withKeyId(KEY_ID);
        request.withPlaintext(ByteBuffer.wrap(plain.getBytes(StandardCharsets.UTF_8)));
        request.withEncryptionAlgorithm(EncryptionAlgorithmSpec.RSAES_OAEP_SHA_256);

        byte[] cipherBytes = awsKmsBuilder.build().encrypt(request).getCiphertextBlob().array();
        return Base64.encodeBase64String(cipherBytes);
    }

    public String decrypt(String cipher) {

        DecryptRequest request = new DecryptRequest();
        request.withKeyId(KEY_ID);
        request.withCiphertextBlob(ByteBuffer.wrap(Base64.decodeBase64(cipher)));
        request.withEncryptionAlgorithm(EncryptionAlgorithmSpec.RSAES_OAEP_SHA_256);

        byte[] textBytes = awsKmsBuilder.build().decrypt(request).getPlaintext().array();
        return new String(textBytes);
    }
}
