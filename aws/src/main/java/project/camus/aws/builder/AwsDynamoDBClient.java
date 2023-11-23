package project.camus.aws.builder;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import project.camus.aws.common.CamusAwsCredentials;

public abstract class AwsDynamoDBClient implements CamusAwsCredentials {

    protected AmazonDynamoDB getClient() {

        return AmazonDynamoDBClientBuilder.standard()
            .withCredentials(getAwsCredentialsProvider())
            .withRegion(getRegions())
            .build();
    }
}
