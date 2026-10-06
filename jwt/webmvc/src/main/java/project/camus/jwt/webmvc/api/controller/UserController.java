package project.camus.jwt.webmvc.api.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.camus.common.SuccessResponse;
import project.camus.webmvc.common.ResponseWrapper;

@RestController
@RequestMapping("/users")
public class UserController {

    /**
     * 토큰의 사용자(sub)와 권한을 돌려준다.
     */
    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<Map<String, Object>>> me(JwtAuthenticationToken authentication) {

        List<String> authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .sorted()
            .toList();
        return ResponseWrapper.success(Map.of("username", authentication.getName(), "authorities", authorities));
    }
}
