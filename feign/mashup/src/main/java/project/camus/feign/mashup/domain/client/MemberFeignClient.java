package project.camus.feign.mashup.domain.client;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import project.camus.feign.mashup.domain.client.config.FeignConfig;

@FeignClient(name = "memberFeignClient",
    url = "${feign-url.member}",
    configuration = FeignConfig.class)
public interface MemberFeignClient {

    @GetMapping(path = "/{memberId}")
    Response findMemberByMemberId(@PathVariable Long memberId);
}
