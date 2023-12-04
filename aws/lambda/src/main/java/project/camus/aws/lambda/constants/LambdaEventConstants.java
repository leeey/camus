package project.camus.aws.lambda.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LambdaEventConstants {

    public static final String HEADERS = "headers";

    public static final String REQUEST_CONTEXT = "requestContext";

    public static final String QUERY_STRING_PARAMETERS = "queryStringParameters";

    public static final String PATH_PARAMETERS = "pathParameters";

    public static final String BODY = "body";
}
