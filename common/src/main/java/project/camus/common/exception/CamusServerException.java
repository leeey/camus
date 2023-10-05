package project.camus.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CamusServerException extends RuntimeException {

  private final HttpStatus httpStatus;

  public CamusServerException(Throwable cause) {
    super(cause);
    this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
