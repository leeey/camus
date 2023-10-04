package project.kamus.infra.jpa.model.member

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import project.kamus.common.util.ObjectMapperUtil
import project.kamus.infra.jpa.config.JpaConfig
import project.kamus.infra.jpa.config.JpaEntityAuditAware
import spock.lang.Specification

@EnableAutoConfiguration
@DataJpaTest
@ContextConfiguration(classes = [MemberDao, ObjectMapper, JpaConfig, JpaEntityAuditAware])
class JpaMemberDaoTest extends Specification {

    @Autowired
    MemberDao memberDao

    ObjectMapper objectMapper

    def setup() {
        objectMapper = ObjectMapperUtil.getInstance()
    }

    def "test"() {

        given:
        def entities = List.of(
                MemberEntity.builder().username("A").phone("123").build(),
                MemberEntity.builder().username("B").phone("234").build(),
                MemberEntity.builder().username("C").phone("567").build(),
        )

        when: "createMembers"
        def createdMembers = memberDao.createMembers(entities)

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
        def allMembers = memberDao.finaAllMembers()

        then:
        !allMembers.empty
        allMembers.size() == createdMembers.size()

        when: "createMember"
        def entity = MemberEntity.builder().username("D").phone("987").build()
        def createdMember = memberDao.createMember(entity)

        then:
        createdMember != null
        createdMember.createdAt != null
        createdMember.createdBy != null
        createdMember.lastModifiedAt != null
        createdMember.lastModifiedBy != null

        when: "diff between allMembers and latestAllMembers"
        def latestAllMembers = memberDao.finaAllMembers()

        then:
        allMembers.size() != latestAllMembers.size()
        allMembers.size() + 1 == latestAllMembers.size()
    }
}