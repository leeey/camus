package project.camus.hexagonal

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.client.RestClient
import org.testcontainers.DockerClientFactory
import org.testcontainers.postgresql.PostgreSQLContainer
import spock.lang.Requires
import spock.lang.Specification

@Requires({ DockerClientFactory.instance().isDockerAvailable() })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskApiIntegrationTest extends Specification {

    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine")

    static {
        if (DockerClientFactory.instance().isDockerAvailable()) {
            postgres.start()
        }
    }

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", { postgres.jdbcUrl })
        registry.add("spring.datasource.username", { postgres.username })
        registry.add("spring.datasource.password", { postgres.password })
    }

    @Value('${local.server.port}')
    int port

    @Autowired
    JdbcTemplate jdbcTemplate

    RestClient client

    def setup() {

        client = RestClient.builder()
            .baseUrl("http://localhost:$port/v1/tasks")
            .defaultStatusHandler({ true }, { request, response -> })
            .build()
    }

    def "flyway migration is applied"() {

        expect:
        jdbcTemplate.queryForObject("SELECT count(*) FROM flyway_schema_history WHERE success", Integer) >= 1
    }

    def "create, archive and delete task"() {

        when: "create"
        def created = client.post()
            .contentType(MediaType.APPLICATION_JSON)
            .body([title: "title", content: "content", priorityType: "HIGH"])
            .retrieve()
            .toEntity(Map)
        def taskId = created.body.result.task.id as Long

        then:
        created.statusCode.value() == 200
        created.body.result.task.archived == false
        with(jdbcTemplate.queryForMap("SELECT * FROM task WHERE id = ?", taskId)) {
            it.title == "title"
            it.created_at != null
            it.created_by != null
        }

        when: "archive"
        def archived = client.put().uri("/{id}/archive", taskId).retrieve().toEntity(Map)

        then:
        archived.statusCode.value() == 200
        archived.body.result.task.archived == true
        jdbcTemplate.queryForObject("SELECT created_at IS NOT NULL FROM task WHERE id = ?", Boolean, taskId)

        when: "delete"
        def deleted = client.delete().uri("/{id}", taskId).retrieve().toBodilessEntity()

        then:
        deleted.statusCode.value() == 200
        jdbcTemplate.queryForObject("SELECT count(*) FROM task WHERE id = ?", Integer, taskId) == 0
    }

    def "archive or delete unknown task returns 404"() {

        when:
        def archived = client.put().uri("/{id}/archive", 999_999).retrieve().toEntity(Map)
        def deleted = client.delete().uri("/{id}", 999_999).retrieve().toEntity(Map)

        then:
        archived.statusCode.value() == 404
        archived.body.errors == ["task not found. id=999999"]
        deleted.statusCode.value() == 404
    }
}
