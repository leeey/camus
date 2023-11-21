package project.camus.observability.member.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.camus.common.SuccessResponse;
import project.camus.observability.member.api.controller.delegator.MemberDelegator;
import project.camus.observability.member.domain.dto.MemberDto;
import project.camus.webmvc.common.ResponseWrapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("/camus/v1/members")
public class MemberController {

    private final MemberDelegator memberDelegator;

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<MemberDto>> findMemberById(@PathVariable("id") Long id) {

        return ResponseWrapper.success(memberDelegator.findMemberById(id));
    }
}
