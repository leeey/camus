package project.camus.observability.member.api.service;

import org.springframework.stereotype.Service;
import project.camus.observability.member.domain.dto.MemberDto;

@Service
public class MemberDummyService {

    public MemberDto findMemberById(Long id) {

        return MemberDto.builder()
            .id(id)
            .name("Test")
            .age(10)
            .address("Seoul")
            .build();
    }
}
