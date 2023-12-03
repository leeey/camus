package project.camus.aws.client.builder;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.springframework.stereotype.Component;
import project.camus.aws.client.common.CamusAwsCredentials;

@Component
public class AwsDynamoDBBuilder implements CamusAwsCredentials {

    public AmazonDynamoDB build() {

        return AmazonDynamoDBClientBuilder.standard()
            .withCredentials(getAwsCredentialsProvider())
            .withRegion(getRegions())
            .build();
    }
}
