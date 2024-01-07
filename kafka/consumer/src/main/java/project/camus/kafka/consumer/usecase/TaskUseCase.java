package project.camus.kafka.consumer.usecase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import project.camus.common.util.ObjectMapperUtil;
import project.camus.kafka.consumer.listener.dto.TaskDto;

@Slf4j
@Component
public class TaskUseCase {

    public void process(TaskDto task) {

        log.info("TaskUseCase::process : {}", ObjectMapperUtil.toJson(task));
    }
}
