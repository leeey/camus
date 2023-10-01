package project.laflex.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LaflexServerException extends RuntimeException {

  private final HttpStatus httpStatus;

  public LaflexServerException(Throwable cause) {
    super(cause);
    this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
