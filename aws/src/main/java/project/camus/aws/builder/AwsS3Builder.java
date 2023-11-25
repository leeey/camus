package project.camus.aws.builder;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import project.camus.aws.common.CamusAwsCredentials;

public abstract class AwsS3Builder implements CamusAwsCredentials {

    public AmazonS3 build() {

        return AmazonS3ClientBuilder.standard()
            .withCredentials(getAwsCredentialsProvider())
            .withRegion(getRegions())
            .build();
    }
}
