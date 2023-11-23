package project.camus.aws.client;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.EncryptionAlgorithmSpec;
import com.amazonaws.services.s3.AmazonS3;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;
import project.camus.aws.adapter.AwsKmsAdapter;

@Component
public class AwsKmsClient extends AwsKmsAdapter {

    public String encrypt(String text) {

        EncryptRequest request = new EncryptRequest();
        request.withKeyId(KEY_ID);
        request.withPlaintext(ByteBuffer.wrap(text.getBytes(StandardCharsets.UTF_8)));
        request.withEncryptionAlgorithm(EncryptionAlgorithmSpec.RSAES_OAEP_SHA_256);

        byte[] cipherBytes = getClient().encrypt(request).getCiphertextBlob().array();
        return Base64.encodeBase64String(cipherBytes);
    }

    public String decrypt(String cipherBase64) {

        DecryptRequest request = new DecryptRequest();
        request.withKeyId(KEY_ID);
        request.withCiphertextBlob(ByteBuffer.wrap(Base64.decodeBase64(cipherBase64)));
        request.withEncryptionAlgorithm(EncryptionAlgorithmSpec.RSAES_OAEP_SHA_256);

        byte[] textBytes = getClient().decrypt(request).getPlaintext().array();
        return new String(textBytes);
    }
}
