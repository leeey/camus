package project.kamus.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class KamusServerException extends RuntimeException {

  private final HttpStatus httpStatus;

  public KamusServerException(Throwable cause) {
    super(cause);
    this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
