package project.laflex.infra.jpa.member;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberDao {

  private final MemberRepository memberRepository;

  public List<MemberEntity> finaAllMembers() {

    return memberRepository.findAll();
  }

  public List<MemberEntity> createMembers(List<MemberEntity> members) {

    return memberRepository.saveAll(members);
  }

  MemberEntity createMember(MemberEntity member) {

    return memberRepository.save(member);
  }
}
