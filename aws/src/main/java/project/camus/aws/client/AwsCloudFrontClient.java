package project.camus.aws.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.camus.aws.builder.AwsCloudFrontBuilder;

@Component
@RequiredArgsConstructor
public class AwsCloudFrontClient {

    private final AwsCloudFrontBuilder awsCloudFrontBuilder;
}
