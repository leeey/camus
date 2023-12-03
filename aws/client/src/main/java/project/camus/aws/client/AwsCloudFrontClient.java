package project.camus.aws.client;

import project.camus.aws.client.builder.AwsCloudFrontBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AwsCloudFrontClient {

    private final AwsCloudFrontBuilder awsCloudFrontBuilder;
}
