package project.camus.observability.mashup.api.controller.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.camus.observability.mashup.domain.dto.member.MemberDto;
import project.camus.observability.mashup.domain.dto.task.TaskDto;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberTaskResponse {

    MemberDto member;

    List<TaskDto> tasks;

    /**
     * 일부 하위 서비스 장애로 tasks 가 비어 있는 부분 응답인지 여부
     */
    boolean degraded;
}
