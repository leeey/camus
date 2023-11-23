package project.camus.aws.adapter;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import project.camus.aws.common.CamusAwsCredentials;

public abstract class AwsS3Adapter implements CamusAwsCredentials {

    protected AmazonS3 getClient() {

        return AmazonS3ClientBuilder.standard()
            .withCredentials(getAwsCredentialsProvider())
            .withRegion(getRegions())
            .build();
    }
}
