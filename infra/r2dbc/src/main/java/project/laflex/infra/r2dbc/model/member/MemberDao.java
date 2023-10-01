package project.laflex.infra.r2dbc.model.member;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MemberDao {

  private final MemberRepository memberRepository;

  public Flux<MemberEntity> finaAllMembers() {

    return memberRepository.findAll();
  }

  public Flux<MemberEntity> createMembers(List<MemberEntity> members) {

    return memberRepository.saveAll(members);
  }

  Mono<MemberEntity> createMember(MemberEntity member) {

    return memberRepository.save(member);
  }
}
