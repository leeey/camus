package project.camus.kafka.consumer

import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.Mockito.timeout
import static org.mockito.Mockito.verify

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.testcontainers.DockerClientFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.kafka.ConfluentKafkaContainer
import project.camus.kafka.avro.Task
import project.camus.kafka.consumer.listener.dto.TaskDto
import project.camus.kafka.consumer.usecase.TaskUseCase
import spock.lang.Requires
import spock.lang.Specification

@Requires({ DockerClientFactory.instance().isDockerAvailable() })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TaskTopicListenerIntegrationTest extends Specification {

    static final String CONFLUENT_VERSION = "8.1.6"

    static Network network = Network.newNetwork()

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
            kafka.start()
            schemaRegistry.dependsOn(kafka).start()
        }
    }

    static String schemaRegistryUrl() {

        "http://${schemaRegistry.host}:${schemaRegistry.getMappedPort(8081)}"
    }

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {

        registry.add("kafka.bootstrap-servers", { kafka.bootstrapServers })
        registry.add("kafka.schema-registry-url", { schemaRegistryUrl() })
        registry.add("spring.kafka.bootstrap-servers", { kafka.bootstrapServers })
    }

    @MockitoSpyBean
    TaskUseCase taskUseCase

    def "consume avro task message"() {

        given:
        def producer = new KafkaProducer<String, Task>([
            (ProducerConfig.BOOTSTRAP_SERVERS_CONFIG)                     : kafka.bootstrapServers,
            (ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG)                  : StringSerializer,
            (ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG)                : KafkaAvroSerializer,
            (AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG)   : schemaRegistryUrl(),
        ] as Map<String, Object>)
        def task = Task.newBuilder().setTitle("title").setDetails("details").setAuthor("camus").build()

        when:
        producer.send(new ProducerRecord<>("task", task)).get()

        then:
        verify(taskUseCase, timeout(30_000)).process(argThat { TaskDto dto ->
            dto.title == "title" && dto.details == "details" && dto.author == "camus"
        })

        cleanup:
        producer?.close()
    }
}
