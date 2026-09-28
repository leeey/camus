package project.camus.aws.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("camus.aws")
public record AwsProperties(@DefaultValue("ap-northeast-2") String region, @DefaultValue Kms kms) {

    public record Kms(String keyId) {

    }
}
