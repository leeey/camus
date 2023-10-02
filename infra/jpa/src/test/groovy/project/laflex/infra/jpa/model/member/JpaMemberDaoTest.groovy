package project.laflex.infra.jpa.model.member

import com.fasterxml.jackson.databind.ObjectMapper
import org.instancio.Instancio
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@EnableAutoConfiguration
@DataJpaTest
@ContextConfiguration(classes = [MemberDao, ObjectMapper])
class JpaMemberDaoTest extends Specification {

    @Autowired
    MemberDao memberDao

    @Autowired
    ObjectMapper objectMapper

    def "test"() {

        given:
        def entities = Instancio.createList(MemberEntity.class)

        when: "createMembers"
        def createdMembers = memberDao.createMembers(entities)

        then:
        !createdMembers.empty
        createdMembers.size() == entities.size()

        when: "finaAllMembers"
        def allMembers = memberDao.finaAllMembers()

        then:
        !allMembers.empty
        allMembers.size() == createdMembers.size()

        when: "createMember"
        def entity = Instancio.create(MemberEntity.class)
        def createdMember = memberDao.createMember(entity)

        then:
        createdMember != null

        when: "diff between allMembers and latestAllMembers"
        def latestAllMembers = memberDao.finaAllMembers()

        then:
        allMembers.size() != latestAllMembers.size()
        allMembers.size() + 1 == latestAllMembers.size()
    }
}