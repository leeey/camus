package project.camus.aws.client.builder;

import project.camus.aws.client.common.CamusAwsCredentials;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import org.springframework.stereotype.Component;

@Component
public class AwsKmsBuilder implements CamusAwsCredentials {

    public AWSKMS build() {

        return AWSKMSClientBuilder.standard()
            .withCredentials(getAwsCredentialsProvider())
            .withRegion(getRegions())
            .build();
    }
}
