package project.camus.jwt.webmvc.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.camus.common.SuccessResponse;
import project.camus.jwt.webmvc.api.dto.request.CreateJwtRequest;
import project.camus.jwt.webmvc.api.service.UserService;
import project.camus.webmvc.common.ResponseWrapper;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/jwt")
    public ResponseEntity<SuccessResponse<String>> getJwt(@RequestBody CreateJwtRequest request) {

        return ResponseWrapper.success(userService.getJwt(request));
    }

    @GetMapping("/username")
    public ResponseEntity<SuccessResponse<String>> getUsername() {

        return ResponseWrapper.success(userService.getUsername());
    }
}
