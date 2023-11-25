package project.camus.aws.builder;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import org.springframework.stereotype.Component;
import project.camus.aws.common.CamusAwsCredentials;

@Component
public class AwsKmsBuilder implements CamusAwsCredentials {

    public AWSKMS build() {

        return AWSKMSClientBuilder.standard()
            .withCredentials(getAwsCredentialsProvider())
            .withRegion(getRegions())
            .build();
    }
}
