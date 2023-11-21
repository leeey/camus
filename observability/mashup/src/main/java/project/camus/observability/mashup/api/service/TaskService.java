package project.camus.observability.mashup.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import feign.Response;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.camus.common.SuccessResponse;
import project.camus.common.exception.CamusServerException;
import project.camus.common.util.ObjectMapperUtil;
import project.camus.observability.mashup.domain.client.TaskFeignClient;
import project.camus.observability.mashup.domain.dto.task.TaskDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskFeignClient taskFeignClient;

    public List<TaskDto> findTasksByMemberId(Long memberId) {

        try (Response response = taskFeignClient.findTasksByMemberId(memberId)) {
            return ObjectMapperUtil.getInstance().readValue(response.body().asReader(StandardCharsets.UTF_8),
                new TypeReference<SuccessResponse<List<TaskDto>>>() {
                }).getResult();
        } catch (IOException e) {
            throw new CamusServerException(e);
        }
    }

}
