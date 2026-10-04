package project.camus.hexagonal

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.DockerClientFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.kafka.ConfluentKafkaContainer
import org.testcontainers.postgresql.PostgreSQLContainer
import spock.lang.Requires
import spock.lang.Specification

/**
 * postgresql, kafka, schema registry 컨테이너를 테스트 JVM 에서 한 번만 띄워 모든 통합 테스트가 공유한다.
 */
@Requires({ DockerClientFactory.instance().isDockerAvailable() })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class IntegrationTestSupport extends Specification {

    static final String CONFLUENT_VERSION = "8.1.6"

    static Network network = Network.newNetwork()

    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine")

    static ConfluentKafkaContainer kafka = new ConfluentKafkaContainer("confluentinc/cp-kafka:$CONFLUENT_VERSION")
        .withNetwork(network)
        .withListener("kafka:19092")

    static GenericContainer schemaRegistry = new GenericContainer("confluentinc/cp-schema-registry:$CONFLUENT_VERSION")
        .withNetwork(network)
        .withExposedPorts(8081)
        .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
        .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
        .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "PLAINTEXT://kafka:19092")
        .waitingFor(Wait.forHttp("/subjects").forStatusCode(200))

    static {
        if (DockerClientFactory.instance().isDockerAvailable()) {
            postgres.start()
            kafka.start()
            schemaRegistry.dependsOn(kafka).start()
        }
    }

    static String schemaRegistryUrl() {

        "http://${schemaRegistry.host}:${schemaRegistry.getMappedPort(8081)}"
    }

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", { postgres.jdbcUrl })
        registry.add("spring.datasource.username", { postgres.username })
        registry.add("spring.datasource.password", { postgres.password })
        registry.add("spring.kafka.bootstrap-servers", { kafka.bootstrapServers })
        registry.add("spring.kafka.properties.schema.registry.url", { schemaRegistryUrl() })
        // 테스트 브로커는 1개
        registry.add("camus.kafka.topics.task-events.replicas", { 1 })
        registry.add("camus.kafka.topics.task-events.min-insync-replicas", { 1 })
        registry.add("camus.outbox.relay.fixed-delay", { "200ms" })
        registry.add("eureka.client.enabled", { false })
    }
}
