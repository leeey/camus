package project.camus.common.exception;

import lombok.Getter;

@Getter
public class CamusClientException extends RuntimeException {

    public CamusClientException(Throwable cause) {
        super(cause);
    }
}
