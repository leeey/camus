package project.camus.database.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.DockerClientFactory
import org.testcontainers.containers.GenericContainer
import spock.lang.Requires
import spock.lang.Specification

@Requires({ DockerClientFactory.instance().isDockerAvailable() })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RedisTaskIntegrationTest extends Specification {

    static GenericContainer redis = new GenericContainer("redis:7.4-alpine").withExposedPorts(6379)

    static {
        if (DockerClientFactory.instance().isDockerAvailable()) {
            redis.start()
        }
    }

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.data.redis.host", { redis.host })
        registry.add("spring.data.redis.port", { redis.getMappedPort(6379) })
    }

    @Value('${local.server.port}')
    int port

    def "create task and get it from redis"() {

        given:
        def client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
        def task = [id: "task-1", userId: "user-1", title: "title", content: "content"]

        when:
        def created = client.post().uri("/redis/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(task)
            .exchange()

        then:
        created.expectStatus().isOk()

        when:
        def found = client.get().uri("/redis/tasks/{taskId}", "task-1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        then:
        found.expectStatus().isOk()
            .expectBody()
            .jsonPath('$.result.id').isEqualTo("task-1")
            .jsonPath('$.result.userId').isEqualTo("user-1")
            .jsonPath('$.result.title').isEqualTo("title")
    }
}
