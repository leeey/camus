package project.camus.aws.client.builder;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.stereotype.Component;
import project.camus.aws.client.common.CamusAwsCredentials;

@Component
public class AwsS3Builder implements CamusAwsCredentials {

    public AmazonS3 build() {

        return AmazonS3ClientBuilder.standard()
            .withCredentials(getAwsCredentialsProvider())
            .withRegion(getRegions())
            .build();
    }
}
