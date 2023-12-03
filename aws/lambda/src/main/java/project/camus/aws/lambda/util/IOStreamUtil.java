package project.camus.aws.lambda.util;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import project.camus.common.util.ObjectMapperUtil;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IOStreamUtil {

    public static PrintWriter getWriter(OutputStream output) {

        return new PrintWriter(new BufferedWriter(new OutputStreamWriter(output)));
    }
}
