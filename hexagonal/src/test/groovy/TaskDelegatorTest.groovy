import org.instancio.Instancio
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import project.camus.hexagonal.domain.task.TaskService
import project.camus.hexagonal.infra.task.adapter.TaskAdapter
import project.camus.hexagonal.port.task.TaskPort
import project.camus.hexagonal.usecase.task.TaskUseCase
import project.camus.hexagonal.web.controller.task.delegator.TaskFindAllDelegator
import project.camus.database.jpa.model.task.TaskDao
import project.camus.database.jpa.model.task.TaskEntity
import spock.lang.Specification

class TaskDelegatorTest extends Specification {

    Pageable pageable
    def taskDao = Mock(TaskDao)

    def taskAdapter = Spy(TaskAdapter, constructorArgs: [taskDao]) as TaskAdapter
    def taskService = Spy(TaskService, constructorArgs: [taskAdapter]) as TaskService
    def taskPort = Spy(TaskPort, constructorArgs: [taskService]) as TaskPort
    def useCase = Spy(TaskUseCase, constructorArgs: [taskPort]) as TaskUseCase

    def setup() {
        pageable = PageRequest.of(0, 20)
    }

    def "findAllTasks"() {
        given:
        def entities = Instancio.createList(TaskEntity)
        def page = new PageImpl<>(entities, pageable, entities.size())
        def delegator = Spy(TaskFindAllDelegator, constructorArgs: [useCase]) as TaskFindAllDelegator

        when:
        taskDao.findAllByPage(_ as Pageable) >> page
        def result = delegator.findAllTasks(pageable)

        then:
        page.getContent().size() == result.tasks.size()
        1 * delegator.findAllTasks(_)
        1 * useCase.findAllTasks(_)
        1 * taskPort.findAllTasks(_)
        1 * taskService.findAllTasks(_)
        1 * taskAdapter.findAllTasks(_)
    }
}