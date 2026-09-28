package project.camus.aws.client.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@EnableConfigurationProperties(AwsProperties.class)
public class AwsClientConfig {

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {

        return DefaultCredentialsProvider.builder().build();
    }

    @Bean
    public KmsClient kmsClient(AwsCredentialsProvider credentialsProvider, AwsProperties properties) {

        return KmsClient.builder()
            .credentialsProvider(credentialsProvider)
            .region(Region.of(properties.region()))
            .build();
    }

    @Bean
    public S3Client s3Client(AwsCredentialsProvider credentialsProvider, AwsProperties properties) {

        return S3Client.builder()
            .credentialsProvider(credentialsProvider)
            .region(Region.of(properties.region()))
            .build();
    }

    @Bean
    public DynamoDbClient dynamoDbClient(AwsCredentialsProvider credentialsProvider, AwsProperties properties) {

        return DynamoDbClient.builder()
            .credentialsProvider(credentialsProvider)
            .region(Region.of(properties.region()))
            .build();
    }

    @Bean
    public CloudFrontClient cloudFrontClient(AwsCredentialsProvider credentialsProvider) {

        // cloudfront is a global service
        return CloudFrontClient.builder()
            .credentialsProvider(credentialsProvider)
            .region(Region.AWS_GLOBAL)
            .build();
    }
}
