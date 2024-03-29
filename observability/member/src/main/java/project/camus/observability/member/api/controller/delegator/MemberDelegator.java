package project.camus.observability.member.api.controller.delegator;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.camus.observability.member.api.service.MemberDummyService;
import project.camus.observability.member.domain.dto.MemberDto;

@Component
@RequiredArgsConstructor
public class MemberDelegator {

    private final MemberDummyService memberDummyService;

    @Observed(name = "MemberDelegator", contextualName = "findMemberById")
    public MemberDto findMemberById(Long id) {

        return memberDummyService.findMemberById(id);
    }
}
