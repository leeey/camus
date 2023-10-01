package project.laflex.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LaflexClientException extends RuntimeException {

  private final HttpStatus httpStatus;

  public LaflexClientException(Throwable cause) {
    super(cause);
    this.httpStatus = HttpStatus.BAD_REQUEST;
  }
}
