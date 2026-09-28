package project.camus.kafka.producer

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import java.time.Duration
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.DockerClientFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.kafka.ConfluentKafkaContainer
import project.camus.kafka.avro.Task
import project.camus.kafka.producer.controller.request.TaskRequest
import project.camus.kafka.producer.service.TaskService
import spock.lang.Requires
import spock.lang.Specification

@Requires({ DockerClientFactory.instance().isDockerAvailable() })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TaskServiceIntegrationTest extends Specification {

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

    @Autowired
    TaskService taskService

    def "send task message as avro"() {

        given:
        def consumer = new KafkaConsumer<String, Task>([
            (ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG)                  : kafka.bootstrapServers,
            (ConsumerConfig.GROUP_ID_CONFIG)                           : "producer-test",
            (ConsumerConfig.AUTO_OFFSET_RESET_CONFIG)                  : "earliest",
            (ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG)             : StringDeserializer,
            (ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG)           : KafkaAvroDeserializer,
            (AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG): schemaRegistryUrl(),
            (KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG)  : true,
        ] as Map<String, Object>)
        consumer.subscribe(["task"])
        def request = new TaskRequest(title: "title", details: "details")

        when:
        taskService.sendMessage(request)
        def received = []
        def deadline = System.currentTimeMillis() + 30_000
        while (received.isEmpty() && System.currentTimeMillis() < deadline) {
            received.addAll(consumer.poll(Duration.ofSeconds(1)).collect { it.value() })
        }

        then:
        received.size() == 1
        with(received.first() as Task) {
            title == "title"
            details == "details"
            author == "camus"
        }

        cleanup:
        consumer?.close()
    }
}
