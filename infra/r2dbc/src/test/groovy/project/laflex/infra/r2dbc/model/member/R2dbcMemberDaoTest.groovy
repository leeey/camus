package project.laflex.infra.r2dbc.model.member

import com.fasterxml.jackson.databind.ObjectMapper
import org.instancio.Instancio
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@EnableAutoConfiguration
@DataR2dbcTest
@ContextConfiguration(classes = [MemberDao, ObjectMapper])
class R2dbcMemberDaoTest extends Specification {

    @Autowired
    MemberDao memberDao

    @Autowired
    ObjectMapper objectMapper

    def "test"() {

        given:
        def entities = Instancio.createList(MemberEntity.class).stream()
                .map(entity -> entity.toBuilder().id(null).build())
                .toList()

        when: "createMembers"
        def createdMembers = memberDao.createMembers(entities).collectList().block()

        then:
        !createdMembers.empty
        createdMembers.size() == entities.size()

        when: "finaAllMembers"
        def allMembers = memberDao.finaAllMembers().collectList().block()

        then:
        !allMembers.empty
        allMembers.size() == createdMembers.size()

        when: "createMember"
        def entity = Instancio.create(MemberEntity.class).toBuilder().id(null).build()
        def createdMember = memberDao.createMember(entity).block()

        then:
        createdMember != null

        when: "diff between allMembers and latestAllMembers"
        def latestAllMembers = memberDao.finaAllMembers().collectList().block()

        then:
        allMembers.size() != latestAllMembers.size()
        allMembers.size() + 1 == latestAllMembers.size()
    }

}