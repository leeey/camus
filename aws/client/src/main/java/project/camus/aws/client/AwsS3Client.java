package project.camus.aws.client;

import project.camus.aws.client.builder.AwsS3Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AwsS3Client {

    private final AwsS3Builder awsS3Builder;
}
