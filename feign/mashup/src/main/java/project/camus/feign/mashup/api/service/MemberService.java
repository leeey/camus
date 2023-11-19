package project.camus.feign.mashup.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import feign.Response;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.camus.common.SuccessResponse;
import project.camus.common.exception.CamusServerException;
import project.camus.common.util.ObjectMapperUtil;
import project.camus.feign.mashup.domain.client.MemberFeignClient;
import project.camus.feign.mashup.domain.dto.member.MemberDto;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberFeignClient memberFeignClient;

    public MemberDto findMemberByMemberId(Long memberId) {

        try (Response response = memberFeignClient.findMemberByMemberId(memberId)) {
            return ObjectMapperUtil.getInstance().readValue(response.body().asReader(StandardCharsets.UTF_8),
                new TypeReference<SuccessResponse<MemberDto>>() {
                }).getResult();
        } catch (IOException e) {
            throw new CamusServerException(e);
        }
    }
}
