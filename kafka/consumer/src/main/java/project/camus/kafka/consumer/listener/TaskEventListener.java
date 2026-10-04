package project.camus.kafka.consumer.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import project.camus.event.task.TaskEvent;
import project.camus.kafka.consumer.usecase.TaskEventUseCase;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskEventListener {

    private final TaskEventUseCase taskEventUseCase;

    @KafkaListener(
        topics = "${camus.kafka.task-events.topic:task-events}",
        groupId = "${camus.kafka.task-events.group-id:camus-task-view}",
        containerFactory = "taskEventListenerContainerFactory")
    public void listen(ConsumerRecord<String, TaskEvent> record) {

        TaskEvent event = record.value();
        log.debug("task event received. partition={}, offset={}, eventId={}, type={}",
            record.partition(), record.offset(), event.getEventId(), event.getEventType());
        taskEventUseCase.process(event);
    }
}
