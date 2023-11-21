package project.camus.observability.mashup.domain.dto.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    private Long id;

    private Long memberId;

    private String description;

    private String priority;
}
