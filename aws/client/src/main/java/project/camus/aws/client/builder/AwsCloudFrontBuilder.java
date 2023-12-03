package project.camus.aws.client.builder;

import project.camus.aws.client.common.CamusAwsCredentials;
import com.amazonaws.services.cloudfront.AmazonCloudFront;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder;
import org.springframework.stereotype.Component;

@Component
public class AwsCloudFrontBuilder implements CamusAwsCredentials {

    public AmazonCloudFront build() {

        return AmazonCloudFrontClientBuilder.standard()
            .withCredentials(getAwsCredentialsProvider()).withRegion(getRegions())
            .build();
    }
}
