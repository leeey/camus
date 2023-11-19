package project.camus.feign.mashup.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.camus.common.SuccessResponse;
import project.camus.feign.mashup.api.controller.delegator.MemberTaskDelegator;
import project.camus.feign.mashup.api.controller.response.MemberTaskResponse;
import project.camus.webmvc.common.ResponseWrapper;

@RestController
@RequestMapping("/camus/v1/member")
@RequiredArgsConstructor
public class MemberTaskController {

    private final MemberTaskDelegator memberTaskDelegator;

    @GetMapping("/{memberId}/tasks")
    public ResponseEntity<SuccessResponse<MemberTaskResponse>> findTasksByMemberId(@PathVariable("memberId") Long memberId) {

        return ResponseWrapper.success(memberTaskDelegator.findTasksByMemberId(memberId));
    }
}
