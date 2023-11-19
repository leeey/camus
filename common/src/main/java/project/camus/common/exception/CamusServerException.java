package project.camus.common.exception;

import lombok.Getter;

@Getter
public class CamusServerException extends RuntimeException {

    public CamusServerException(Throwable cause) {
        super(cause);
    }

    public CamusServerException(String message) {
        super(message);
    }
}
