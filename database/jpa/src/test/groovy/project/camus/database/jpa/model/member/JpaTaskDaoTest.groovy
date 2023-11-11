package project.camus.database.jpa.model.member

import org.instancio.Instancio
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ContextConfiguration
import project.camus.database.jpa.config.JpaConfig
import project.camus.database.jpa.config.JpaEntityAuditAware
import project.camus.database.jpa.model.task.TaskDao
import project.camus.database.jpa.model.task.TaskEntity
import spock.lang.Specification

@EnableAutoConfiguration
@DataJpaTest
@ContextConfiguration(classes = [TaskDao, JpaConfig, JpaEntityAuditAware])
class JpaTaskDaoTest extends Specification {

    @Autowired
    TaskDao taskDao

    def "test"() {

        given:
        def entity = Instancio.create(TaskEntity)

        when: "createTask"
        def createdTask = taskDao.save(entity)

        then:
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