package project.camus.observability.mashup.domain.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.camus.common.SuccessResponse;
import project.camus.observability.mashup.domain.client.config.FeignConfig;
import project.camus.observability.mashup.domain.dto.task.TaskDto;

@FeignClient(name = "taskFeignClient",
    url = "${feign-url.task}",
    configuration = FeignConfig.class)
public interface TaskFeignClient {

    @GetMapping()
    SuccessResponse<List<TaskDto>> findTasksByMemberId(@RequestParam("memberId") Long memberId);
}
