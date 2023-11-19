package project.camus.feign.mashup.api.controller.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.camus.feign.mashup.domain.dto.member.MemberDto;
import project.camus.feign.mashup.domain.dto.task.TaskDto;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberTaskResponse {

    MemberDto member;

    List<TaskDto> tasks;
}
