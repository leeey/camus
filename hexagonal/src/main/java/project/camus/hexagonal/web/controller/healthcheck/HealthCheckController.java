package project.camus.hexagonal.web.controller.healthcheck;

import java.util.Map;
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

    @GetMapping
    public ResponseEntity<SuccessResponse<Map<String, String>>> healthCheck() {

        return ResponseWrapper.success(Map.of("status", "OK"));
    }
}
