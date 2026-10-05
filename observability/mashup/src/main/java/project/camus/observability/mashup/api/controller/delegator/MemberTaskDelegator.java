package project.camus.observability.mashup.api.controller.delegator;

import io.micrometer.observation.annotation.Observed;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.camus.observability.mashup.api.controller.response.MemberTaskResponse;
import project.camus.observability.mashup.api.service.MemberService;
import project.camus.observability.mashup.api.service.TaskService;
import project.camus.observability.mashup.domain.dto.member.MemberDto;
import project.camus.observability.mashup.domain.dto.task.TaskDto;

@Component
@RequiredArgsConstructor
public class MemberTaskDelegator {

    private final MemberService memberService;

    private final TaskService taskService;

    @Observed(name = "MemberTaskDelegator", contextualName = "findTasksByMemberId")
    public MemberTaskResponse findTasksByMemberId(Long memberId) {

        MemberDto member = memberService.findMemberByMemberId(memberId);
        Optional<List<TaskDto>> tasks = taskService.findTasksByMemberId(memberId);

        return MemberTaskResponse.builder()
            .member(member)
            .tasks(tasks.orElse(List.of()))
            .degraded(tasks.isEmpty())
            .build();
    }
}
