package project.camus.aws.client.builder;

import project.camus.aws.client.common.CamusAwsCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public abstract class AwsS3Builder implements CamusAwsCredentials {

    public AmazonS3 build() {

        return AmazonS3ClientBuilder.standard()
            .withCredentials(getAwsCredentialsProvider())
            .withRegion(getRegions())
            .build();
    }
}
