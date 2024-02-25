package project.camus.aws.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.camus.aws.client.builder.AwsS3Builder;

@Component
@RequiredArgsConstructor
public class AwsS3Client {

    private final AwsS3Builder awsS3Builder;
}
