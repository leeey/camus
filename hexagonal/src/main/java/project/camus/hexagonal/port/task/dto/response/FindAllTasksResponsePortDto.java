package project.camus.hexagonal.port.task.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import project.camus.hexagonal.port.task.dto.TaskPortDto;

@Builder
@Getter
public class FindAllTasksResponsePortDto {

    List<TaskPortDto> tasks;
}
