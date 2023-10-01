package project.laflex.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import project.laflex.common.exception.LaflexClientException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceUtil {

  public static ClassPathResource getPathResource(String path) {

    if (StringUtils.isBlank(path)) {
      throw new LaflexClientException(new IllegalArgumentException("path is invalid."));
    }
    return new ClassPathResource(path);
  }
}
