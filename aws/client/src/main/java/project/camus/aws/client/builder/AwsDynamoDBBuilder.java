package project.camus.aws.client.builder;

import project.camus.aws.client.common.CamusAwsCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

public abstract class AwsDynamoDBBuilder implements CamusAwsCredentials {

    public AmazonDynamoDB build() {

        return AmazonDynamoDBClientBuilder.standard()
            .withCredentials(getAwsCredentialsProvider())
            .withRegion(getRegions())
            .build();
    }
}
