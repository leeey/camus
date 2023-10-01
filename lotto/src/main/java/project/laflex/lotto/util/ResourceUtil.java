package project.laflex.lotto.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ClassPathResource;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceUtil {

  public static ClassPathResource getPathResource(String path) {

    return new ClassPathResource(path);
  }
}
