package project.camus.hexagonal.port.task.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.camus.hexagonal.port.task.dto.TaskPortDto;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FindAllTasksResponsePortDto {

    List<TaskPortDto> tasks;
}
