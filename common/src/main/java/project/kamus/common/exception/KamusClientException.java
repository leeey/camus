package project.kamus.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class KamusClientException extends RuntimeException {

  private final HttpStatus httpStatus;

  public KamusClientException(Throwable cause) {
    super(cause);
    this.httpStatus = HttpStatus.BAD_REQUEST;
  }
}
