package project.camus.kafka.producer.controller.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskRequest {

    private String title;

    private String details;
}
