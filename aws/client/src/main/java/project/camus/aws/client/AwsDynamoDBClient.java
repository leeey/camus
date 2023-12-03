package project.camus.aws.client;

import project.camus.aws.client.builder.AwsDynamoDBBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AwsDynamoDBClient {

    private final AwsDynamoDBBuilder awsDynamoDBBuilder;
}
