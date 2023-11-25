package project.camus.aws.builder;

import com.amazonaws.services.cloudfront.AmazonCloudFront;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder;
import org.springframework.stereotype.Component;
import project.camus.aws.common.CamusAwsCredentials;

@Component
public class AwsCloudFrontBuilder implements CamusAwsCredentials {

    public AmazonCloudFront build() {

        return AmazonCloudFrontClientBuilder.standard()
            .withCredentials(getAwsCredentialsProvider()).withRegion(getRegions())
            .build();
    }
}
