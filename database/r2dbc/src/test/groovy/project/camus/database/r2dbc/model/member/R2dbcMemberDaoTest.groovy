package project.camus.database.r2dbc.model.member

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@EnableAutoConfiguration
@DataR2dbcTest
@ContextConfiguration(classes = [MemberDao, ObjectMapper, project.camus.database.r2dbc.config.R2dbcConfig, project.camus.database.r2dbc.config.R2dbcEntityAuditAware])
class R2dbcMemberDaoTest extends Specification {

    @Autowired
    MemberDao memberDao

    def "test"() {

        given:
        def entities = List.of(
                MemberEntity.builder().username("A").phone("123").build(),
                MemberEntity.builder().username("B").phone("234").build(),
                MemberEntity.builder().username("C").phone("567").build(),
        )

        when: "createMembers"
        def createdMembers = memberDao.createMembers(entities).collectList().block()

        then:
        !createdMembers.empty
        createdMembers.size() == entities.size()
        createdMembers.forEach(member -> {
            member.createdAt != null
            member.createdBy != null
            member.lastModifiedAt != null
            member.lastModifiedBy != null
        })

        when: "finaAllMembers"
        def allMembers = memberDao.finaAllMembers().collectList().block()

        then:
        !allMembers.empty
        allMembers.size() == createdMembers.size()

        when: "createMember"
        def entity = MemberEntity.builder().username("D").phone("987").build()
        def createdMember = memberDao.createMember(entity).block()

        then:
        createdMember != null
        createdMember.createdAt != null
        createdMember.createdBy != null
        createdMember.lastModifiedAt != null
        createdMember.lastModifiedBy != null

        when: "diff between allMembers and latestAllMembers"
        def latestAllMembers = memberDao.finaAllMembers().collectList().block()

        then:
        allMembers.size() != latestAllMembers.size()
        allMembers.size() + 1 == latestAllMembers.size()
    }
}