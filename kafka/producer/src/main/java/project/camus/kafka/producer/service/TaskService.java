package project.camus.kafka.producer.service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import project.camus.kafka.avro.Task;
import project.camus.kafka.producer.controller.request.TaskRequest;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class TaskService {

    private static final String TOPIC = "task-topic";

    private final KafkaTemplate<String, Task> kafkaTemplate;

    public void sendMessage(@NonNull TaskRequest request) {

        Task task = Task.newBuilder()
            .setTitle(request.getTitle())
            .setDetails(request.getDetails())
            .build();
        CompletableFuture<SendResult<String, Task>> future = sendMessage(task);
        future.whenComplete((result, ex) -> {
            if (Objects.isNull(ex)) {
                log.info("Sent message=[{}] with offset=[{}]", task, result.getRecordMetadata().offset());
            } else {
                log.info("Unable to send message=[{}] due to : {}", task, ex.getMessage());
            }
        });
    }

    private CompletableFuture<SendResult<String, Task>> sendMessage(Task task) {

        return kafkaTemplate.send(new ProducerRecord<>(TOPIC, UUID.randomUUID().toString(), task));
    }
}
