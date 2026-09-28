package project.camus.aws.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Component
@RequiredArgsConstructor
public class AwsDynamoDBClient {

    private final DynamoDbClient dynamoDbClient;
}
