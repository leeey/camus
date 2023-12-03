package project.camus.aws.client.common;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;

public interface CamusAwsCredentials {

    default AWSCredentialsProvider getAwsCredentialsProvider() {

        return new DefaultAWSCredentialsProviderChain();
    }

    default Regions getRegions() {

        return Regions.AP_NORTHEAST_2;
    }
}
