package project.camus.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CamusClientException extends RuntimeException {

  private final HttpStatus httpStatus;

  public CamusClientException(Throwable cause) {
    super(cause);
    this.httpStatus = HttpStatus.BAD_REQUEST;
  }
}
