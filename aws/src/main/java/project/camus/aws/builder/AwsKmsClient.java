package project.camus.aws.builder;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import project.camus.aws.common.CamusAwsCredentials;

public abstract class AwsKmsClient implements CamusAwsCredentials {

    // this arn is only for test.
    protected static final String KEY_ID = "f096a65c-38d6-4a2b-b0c5-7f5c81636367";

    protected AWSKMS getClient() {

        return AWSKMSClientBuilder.standard()
            .withCredentials(getAwsCredentialsProvider())
            .withRegion(getRegions())
            .build();
    }
}
