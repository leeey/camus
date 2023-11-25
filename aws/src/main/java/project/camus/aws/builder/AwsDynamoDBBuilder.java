package project.camus.aws.builder;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import project.camus.aws.common.CamusAwsCredentials;

public abstract class AwsDynamoDBBuilder implements CamusAwsCredentials {

    public AmazonDynamoDB build() {

        return AmazonDynamoDBClientBuilder.standard()
            .withCredentials(getAwsCredentialsProvider())
            .withRegion(getRegions())
            .build();
    }
}
