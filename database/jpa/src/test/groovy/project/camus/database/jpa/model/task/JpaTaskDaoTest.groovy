package project.camus.database.jpa.model.task

import static org.instancio.Select.field

import org.instancio.Instancio
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ContextConfiguration
import project.camus.database.jpa.config.JpaConfig
import project.camus.database.jpa.config.JpaEntityAuditAware
import project.camus.database.jpa.model.JpaAuditEntity
import spock.lang.Specification

@EnableAutoConfiguration
@DataJpaTest
@ContextConfiguration(classes = [TaskDao, JpaConfig, JpaEntityAuditAware])
class JpaTaskDaoTest extends Specification {

    @Autowired
    TaskDao taskDao

    def "test"() {

        given:
        def entity = Instancio.of(TaskEntity)
                .ignore(field(TaskEntity, "id"))
                .ignore(field(TaskEntity, "version"))
                .ignore(field(JpaAuditEntity, "createdAt"))
                .ignore(field(JpaAuditEntity, "createdBy"))
                .ignore(field(JpaAuditEntity, "lastModifiedAt"))
                .ignore(field(JpaAuditEntity, "lastModifiedBy"))
                .create()

        when: "createTask"
        def createdTask = taskDao.save(entity)

        then:
        createdTask.id != null
        createdTask.createdAt != null
        createdTask.createdBy != null
        createdTask.lastModifiedAt != null
        createdTask.lastModifiedBy != null

        when: "findAllTasks"
        def pageable = PageRequest.of(0, 20)
        def taskPage = taskDao.findAllByPage(pageable)

        then:
        !taskPage.empty

        when: "deleteTask"
        taskDao.delete(createdTask)

        then:
        taskDao.findAllByPage(pageable).getContent().size() == 0
    }
}