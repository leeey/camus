package project.camus.kafka.consumer.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import project.camus.common.exception.CamusServerException;
import project.camus.common.util.ObjectMapperUtil;
import project.camus.kafka.avro.Task;
import project.camus.kafka.consumer.listener.dto.TaskDto;
import project.camus.kafka.consumer.usecase.TaskUseCase;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskTopicListener {

    private static final String TOPIC = "task";

    private final TaskUseCase taskUseCase;

    @KafkaListener(topics = {TOPIC}, groupId = "camus-kafka-group", autoStartup = "true")
    public void listen(ConsumerRecord<String, GenericRecord> consumerRecord) {

        //TODO: should consume record...
        try {
            Task task = ObjectMapperUtil.getInstance()
                .readValue(consumerRecord.value().toString(), Task.class);
            taskUseCase.process(TaskDto.builder()
                .title(task.getTitle())
                .details(task.getDetails())
                .author(task.getAuthor())
                .build());
        } catch (JsonProcessingException e) {
            throw new CamusServerException(e);
        }
    }
}
