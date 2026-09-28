package project.camus.aws.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;

@Component
@RequiredArgsConstructor
public class AwsCloudFrontClient {

    private final CloudFrontClient cloudFrontClient;
}
