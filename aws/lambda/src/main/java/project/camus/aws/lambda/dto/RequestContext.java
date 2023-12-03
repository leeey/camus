package project.camus.aws.lambda.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestContext {
    
    private String accountId;

    private String apiId;

    private String domainName;

    private String domainPrefix;

    private Http http;

    private String requestId;

    private String routeKey;

    private String stage;

    private String time;

    private Long timeEpoch;


    @Data
    public static class Http {

        private String method;

        private String path;

        private String protocol;

        private String sourceIp;

        private String userAgent;
    }
}
