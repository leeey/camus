package project.camus.kafka.consumer.listener.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    private String title;

    private String details;

    private String author;
}
