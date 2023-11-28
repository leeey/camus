package project.camus.kafka.producer.service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import project.camus.kafka.avro.TaskRecord;
import project.camus.kafka.producer.controller.request.TaskRequest;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class TaskService {

    private static final String TOPIC = "task-topic";

    private final KafkaTemplate<String, TaskRecord> kafkaTemplate;

    public void sendMessage(@NonNull TaskRequest request) {

        TaskRecord task = TaskRecord.newBuilder()
            .setTitle(request.getTitle())
            .setDetails(request.getDetails())
            .build();
        CompletableFuture<SendResult<String, TaskRecord>> future = sendMessage(task);
        future.whenComplete((result, ex) -> {
            if (Objects.isNull(ex)) {
                log.info("Sent message=[{}] with offset=[{}]", task, result.getRecordMetadata().offset());
            } else {
                log.info("Unable to send message=[{}] due to : {}", task, ex.getMessage());
            }
        });
    }

    private CompletableFuture<SendResult<String, TaskRecord>> sendMessage(TaskRecord task) {

        return kafkaTemplate.send(new ProducerRecord<>(TOPIC, task));
    }
}
