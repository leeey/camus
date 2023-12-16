package project.camus.jwt.webflux.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.camus.common.exception.CamusClientException;
import project.camus.jwt.webflux.util.JwtRefreshTokenUtil;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JwtCreateTokenRequestDto {

    @NotBlank
    private String refreshToken;

    @NotBlank
    private String username;

    public void validate() {

        if (JwtRefreshTokenUtil.getRefreshTokens().stream()
            .filter(e -> Objects.equals(refreshToken, e.getToken()) &&
                LocalDate.now().isBefore(LocalDate.parse(e.getExpiredAt())))
            .findAny().isEmpty()) {
            throw new CamusClientException(new IllegalArgumentException("token is invalid."));
        }
    }
}
