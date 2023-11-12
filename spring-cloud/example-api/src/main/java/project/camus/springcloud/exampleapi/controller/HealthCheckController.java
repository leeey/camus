package project.camus.springcloud.exampleapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.camus.common.SuccessResponse;
import project.camus.webmvc.common.ResponseWrapper;

@RestController
@RequestMapping("/healthcheck")
@RequiredArgsConstructor
public class HealthCheckController {

    private final HttpServletRequest request;

    @GetMapping
    public ResponseEntity<SuccessResponse<Map<String, Object>>> healthCheck() {

        return ResponseWrapper.ok(Map.of("status", "OK", "port", request.getServerPort()));
    }
}
