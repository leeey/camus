package project.camus.authserver.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * 로그인 사용자 (예시). 운영에서는 사용자 DB 나 외부 IdP 로 바꾼다.
 * password 는 {bcrypt} 처럼 인코딩 방식을 앞에 붙인다.
 */
@ConfigurationProperties("camus.auth")
public record AuthUsersProperties(@DefaultValue List<User> users) {

    public record User(String username, String password, @DefaultValue("USER") List<String> roles) {

    }
}
