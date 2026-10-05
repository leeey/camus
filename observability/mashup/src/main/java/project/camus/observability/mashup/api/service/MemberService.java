package project.camus.observability.mashup.api.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.camus.observability.mashup.domain.client.MemberFeignClient;
import project.camus.observability.mashup.domain.dto.member.MemberDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberFeignClient memberFeignClient;

    /**
     * member 는 응답의 핵심 데이터라 fallback 없이 실패를 그대로 전달한다 (503).
     */
    @Retry(name = "member")
    @CircuitBreaker(name = "member")
    public MemberDto findMemberByMemberId(Long memberId) {

        return memberFeignClient.findMemberByMemberId(memberId).getResult();
    }
}
