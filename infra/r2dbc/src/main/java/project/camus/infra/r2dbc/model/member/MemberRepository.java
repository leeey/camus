package project.camus.infra.r2dbc.model.member;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface MemberRepository extends ReactiveCrudRepository<MemberEntity, Long> {

}
