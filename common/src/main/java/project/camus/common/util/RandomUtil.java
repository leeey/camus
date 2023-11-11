package project.camus.common.util;

import java.security.SecureRandom;
import java.util.Random;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomUtil {

    private static final Random random = new SecureRandom();

    public static int nextInt() {

        return random.nextInt();
    }
}
