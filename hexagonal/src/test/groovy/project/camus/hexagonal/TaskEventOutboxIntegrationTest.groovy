package project.camus.hexagonal

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import java.time.Duration
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.IllegalTransactionStateException
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.client.RestClient
import org.testcontainers.DockerClientFactory
import project.camus.database.jpa.model.task.TaskEntity
import project.camus.event.task.TaskEvent
import project.camus.event.task.TaskEventType
import project.camus.hexagonal.domain.task.TaskService
import project.camus.hexagonal.domain.task.event.TaskDomainEvent
import project.camus.hexagonal.domain.task.event.TaskEventPort

class TaskEventOutboxIntegrationTest extends IntegrationTestSupport {

    @Value('${local.server.port}')
    int port

    @Autowired
    JdbcTemplate jdbcTemplate

    @Autowired
    TransactionTemplate transactionTemplate

    @Autowired
    TaskService taskService

    @Autowired
    TaskEventPort taskEventPort

    def "task changes are published to task-events in order through the outbox"() {

        given:
        def client = RestClient.builder().baseUrl("http://localhost:$port/v1/tasks").build()
        def consumer = createConsumer()

        when:
        def taskId = client.post()
            .contentType(MediaType.APPLICATION_JSON)
            .body([title: "outbox", content: "content", priorityType: "HIGH"])
            .retrieve()
            .body(Map).result.task.id as Long
        client.put().uri("/{id}/archive", taskId).retrieve().toBodilessEntity()
        client.delete().uri("/{id}", taskId).retrieve().toBodilessEntity()
        def records = pollUntil(consumer, 3) { it.value().taskId == taskId }

        then:
        records*.value().eventType == [TaskEventType.CREATED, TaskEventType.ARCHIVED, TaskEventType.DELETED]
        records*.key().every { it == String.valueOf(taskId) }
        records*.value().eventId.unique().size() == 3
        with(records.first().value()) {
            title == "outbox"
            content == "content"
            priority == 3
            !archived
        }
        records[1].value().archived

        and: "outbox rows are marked as published"
        jdbcTemplate.queryForObject(
            "SELECT count(*) FROM outbox_event WHERE aggregate_id = ? AND published_at IS NOT NULL", Integer, taskId) == 3

        cleanup:
        consumer?.close()
    }

    def "request trace continues to the kafka record through the outbox"() {

        given:
        def traceId = "4bf92f3577b34da6a3ce929d0e0e4736"
        def client = RestClient.builder().baseUrl("http://localhost:$port/v1/tasks").build()
        def consumer = createConsumer()

        when:
        def taskId = client.post()
            .contentType(MediaType.APPLICATION_JSON)
            .header("traceparent", "00-$traceId-00f067aa0ba902b7-01")
            .body([title: "traced", content: "content", priorityType: "LOW"])
            .retrieve()
            .body(Map).result.task.id as Long
        def records = pollUntil(consumer, 1) { it.value().taskId == taskId }

        then: "outbox 에 요청의 traceparent 가 저장되고, kafka 헤더도 같은 traceId 로 이어진다"
        jdbcTemplate.queryForObject("SELECT trace_parent FROM outbox_event WHERE aggregate_id = ?", String, taskId)
            .startsWith("00-$traceId-")
        new String(records.first().headers().lastHeader("traceparent").value()).startsWith("00-$traceId-")

        cleanup:
        consumer?.close()
    }

    def "outbox event is rolled back together with the task"() {

        given:
        def title = "rollback-${UUID.randomUUID()}"

        when:
        transactionTemplate.executeWithoutResult { status ->
            taskService.createTask(TaskEntity.builder().title(title).priority(1).build())
            status.setRollbackOnly()
        }

        then:
        jdbcTemplate.queryForObject("SELECT count(*) FROM task WHERE title = ?", Integer, title) == 0
        jdbcTemplate.queryForObject("SELECT count(*) FROM outbox_event WHERE payload ->> 'title' = ?", Integer, title) == 0
    }

    def "outbox append requires an existing transaction"() {

        when:
        taskEventPort.append(new TaskDomainEvent(UUID.randomUUID(), TaskDomainEvent.Type.CREATED, 1L, "t", "c", 1,
            false, java.time.Instant.now()))

        then:
        thrown(IllegalTransactionStateException)
    }

    def "events stored while kafka is unavailable are published after recovery"() {

        given:
        def docker = DockerClientFactory.instance().client()
        def consumer = createConsumer()
        def paused = false

        when: "kafka is paused"
        docker.pauseContainerCmd(kafka.containerId).exec()
        paused = true
        def taskId = transactionTemplate.execute {
            taskService.createTask(TaskEntity.builder().title("while-kafka-down").priority(1).build()).id
        } as Long
        Thread.sleep(1_000)

        then: "task is saved and the event waits in the outbox"
        jdbcTemplate.queryForObject("SELECT count(*) FROM task WHERE id = ?", Integer, taskId) == 1
        jdbcTemplate.queryForObject(
            "SELECT published_at IS NULL FROM outbox_event WHERE aggregate_id = ?", Boolean, taskId)

        when: "kafka is back"
        docker.unpauseContainerCmd(kafka.containerId).exec()
        paused = false
        def records = pollUntil(consumer, 1) { it.value().taskId == taskId }

        then:
        records*.value().eventType == [TaskEventType.CREATED]

        cleanup:
        if (paused) {
            docker.unpauseContainerCmd(kafka.containerId).exec()
        }
        consumer?.close()
    }

    private static KafkaConsumer<String, TaskEvent> createConsumer() {

        def consumer = new KafkaConsumer<String, TaskEvent>([
            (ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG)                  : kafka.bootstrapServers,
            (ConsumerConfig.GROUP_ID_CONFIG)                           : "outbox-test-${UUID.randomUUID()}".toString(),
            (ConsumerConfig.AUTO_OFFSET_RESET_CONFIG)                  : "earliest",
            (ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG)             : StringDeserializer,
            (ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG)           : KafkaAvroDeserializer,
            (AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG): schemaRegistryUrl(),
            (KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG)  : true,
        ] as Map<String, Object>)
        consumer.subscribe(["task-events"])
        consumer
    }

    private static List<ConsumerRecord<String, TaskEvent>> pollUntil(KafkaConsumer<String, TaskEvent> consumer, int count,
        Closure<Boolean> filter) {

        def records = []
        def deadline = System.currentTimeMillis() + 30_000
        while (records.size() < count && System.currentTimeMillis() < deadline) {
            records.addAll(consumer.poll(Duration.ofMillis(500)).findAll(filter))
        }
        records
    }
}
