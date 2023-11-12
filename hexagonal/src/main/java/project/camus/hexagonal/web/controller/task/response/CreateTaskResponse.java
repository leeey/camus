package project.camus.hexagonal.web.controller.task.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;
import project.camus.hexagonal.web.controller.task.dto.TaskDto;

@Builder
@Getter
@JsonInclude(Include.NON_NULL)
public class CreateTaskResponse {

    TaskDto task;
}
