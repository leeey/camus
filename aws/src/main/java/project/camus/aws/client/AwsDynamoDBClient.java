package project.camus.aws.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.camus.aws.builder.AwsDynamoDBBuilder;

@Component
@RequiredArgsConstructor
public class AwsDynamoDBClient {

    private final AwsDynamoDBBuilder awsDynamoDBBuilder;
}
