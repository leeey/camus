package project.camus.aws.lambda.handler.sample;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import project.camus.aws.lambda.constants.LambdaEventConstants;
import project.camus.aws.lambda.handler.sample.request.Headers;
import project.camus.aws.lambda.handler.sample.request.PathParameters;
import project.camus.aws.lambda.handler.sample.request.QueryStringParameters;
import project.camus.aws.lambda.dto.RequestContext;
import project.camus.aws.lambda.util.IOUtil;
import project.camus.common.util.ObjectMapperUtil;

@SuppressWarnings("all")
public class SampleHandler implements RequestStreamHandler {

    private static LambdaLogger LOGGER;

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

        LOGGER = context.getLogger();

        Map<String, Object> result = new HashMap<>();

        try (PrintWriter writer = IOUtil.getWriter(output)) {

            Map<String, Object> event = ObjectMapperUtil.readInputStreamValue(input);
            RequestContext requestContext = ObjectMapperUtil.convertValue(event.get(LambdaEventConstants.REQUEST_CONTEXT), RequestContext.class);
            Headers headers = ObjectMapperUtil.convertValue(event.get(LambdaEventConstants.HEADERS), Headers.class);
            PathParameters pathParameters = ObjectMapperUtil.convertValue(event.get(LambdaEventConstants.PATH_PARAMETERS), PathParameters.class);
            QueryStringParameters queryStringParameters = ObjectMapperUtil.convertValue(event.get(LambdaEventConstants.QUERY_STRING_PARAMETERS),
                QueryStringParameters.class);
            Map<String, Object> body = ObjectMapperUtil.readString(event.get(LambdaEventConstants.BODY).toString());

            //TODO: handle event
            writer.write("");
            writer.flush();
        }
    }
}
