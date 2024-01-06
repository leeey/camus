package project.camus.kafka.consumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import project.camus.kafka.avro.Task;

@Slf4j
@Component
public class TaskTopicListener {

    @KafkaListener(topics = {"task-topic"}, groupId = "camus-kafka-group", autoStartup = "true")
    public void listen(ConsumerRecord<String, Task> consumerRecord) {

        //TODO: should consume record...
        log.info(consumerRecord.toString());
    }
}
