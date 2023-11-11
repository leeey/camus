package project.camus.database.r2dbc.model.member;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MemberDao {

    private final MemberRepository memberRepository;

    private final TransactionalOperator transactionalOperator;

    public Flux<MemberEntity> finaAllMembers() {

        return memberRepository.findAll();
    }

    public Flux<MemberEntity> createMembers(List<MemberEntity> members) {

        return memberRepository.saveAll(members)
            .as(transactionalOperator::transactional);
    }

    Mono<MemberEntity> createMember(MemberEntity member) {

        return memberRepository.save(member)
            .as(transactionalOperator::transactional);
    }
}
