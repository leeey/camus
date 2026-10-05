package project.camus.observability.mashup.domain.client.config;

import lombok.Getter;

/**
 * 하위 서비스가 5xx 를 응답한 경우. 재시도 대상이고 circuit breaker 실패로 기록한다.
 */
@Getter
public class DownstreamUnavailableException extends RuntimeException {

    private final int status;

    public DownstreamUnavailableException(String methodKey, int status) {

        super("downstream unavailable. method=" + methodKey + ", status=" + status);
        this.status = status;
    }
}
