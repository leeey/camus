package project.camus.observability.mashup.domain.client;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.camus.observability.mashup.domain.client.config.FeignConfig;

@FeignClient(name = "taskFeignClient",
    url = "${feign-url.task}",
    configuration = FeignConfig.class)
public interface TaskFeignClient {

    @GetMapping()
    Response findTasksByMemberId(@RequestParam("memberId") Long memberId);
}
