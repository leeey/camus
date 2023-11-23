package project.camus.aws.adapter;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import project.camus.aws.common.CamusAwsCredentials;

public abstract class AwsDynamoDBAdapter implements CamusAwsCredentials {

    protected AmazonDynamoDB getClient() {

        return AmazonDynamoDBClientBuilder.standard()
            .withCredentials(getAwsCredentialsProvider())
            .withRegion(getRegions())
            .build();
    }
}
