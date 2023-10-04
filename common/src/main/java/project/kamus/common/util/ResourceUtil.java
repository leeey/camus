package project.kamus.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import project.kamus.common.exception.KamusClientException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceUtil {

  public static ClassPathResource getPathResource(String path) {

    if (StringUtils.isBlank(path)) {
      throw new KamusClientException(new IllegalArgumentException("path is invalid."));
    }
    return new ClassPathResource(path);
  }
}
