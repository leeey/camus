package project.camus.jwt.webmvc.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JwtRefreshTokenDto {

    private String token;

    private String expiredAt;
}
