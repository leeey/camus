package project.camus.observability.mashup.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import feign.Response;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.camus.common.SuccessResponse;
import project.camus.common.exception.CamusServerException;
import project.camus.common.util.ObjectMapperUtil;
import project.camus.observability.mashup.domain.client.MemberFeignClient;
import project.camus.observability.mashup.domain.dto.member.MemberDto;

@Slf4j
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
