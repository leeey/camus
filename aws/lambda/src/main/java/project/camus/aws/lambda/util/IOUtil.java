package project.camus.aws.lambda.util;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IOUtil {

    public static PrintWriter getWriter(OutputStream output) {

        return new PrintWriter(new BufferedWriter(new OutputStreamWriter(output)));
    }
}
