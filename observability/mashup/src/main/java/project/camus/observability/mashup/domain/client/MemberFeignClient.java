package project.camus.observability.mashup.domain.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import project.camus.common.SuccessResponse;
import project.camus.observability.mashup.domain.client.config.FeignConfig;
import project.camus.observability.mashup.domain.dto.member.MemberDto;

@FeignClient(name = "memberFeignClient",
    url = "${feign-url.member}",
    configuration = FeignConfig.class)
public interface MemberFeignClient {

    @GetMapping(path = "/{memberId}")
    SuccessResponse<MemberDto> findMemberByMemberId(@PathVariable Long memberId);
}
