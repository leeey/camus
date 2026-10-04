package project.camus.kafka.consumer

import static org.mockito.ArgumentMatchers.argThat
import static org.mockito.Mockito.doThrow
import static org.mockito.Mockito.timeout
import static org.mockito.Mockito.verify

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import java.time.Duration
import java.time.Instant
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import project.camus.event.task.TaskEvent
import project.camus.event.task.TaskEventType
import project.camus.kafka.consumer.usecase.TaskEventUseCase
import spock.lang.Shared
import spock.util.concurrent.PollingConditions

class TaskEventListenerIntegrationTest extends IntegrationTestSupport {

    static final String TOPIC = "task-events"

    static final String DLT = "task-events.DLT"

    @Autowired
    JdbcTemplate jdbcTemplate

    @MockitoSpyBean
    TaskEventUseCase taskEventUseCase

    @Shared
    KafkaProducer<String, TaskEvent> producer

    def conditions = new PollingConditions(timeout: 30, delay: 0.2)

    def setupSpec() {

        producer = new KafkaProducer<String, TaskEvent>([
            (ProducerConfig.BOOTSTRAP_SERVERS_CONFIG)                  : kafka.bootstrapServers,
            (ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG)               : StringSerializer,
            (ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG)             : KafkaAvroSerializer,
            (AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG): schemaRegistryUrl(),
        ] as Map<String, Object>)
    }

    def cleanupSpec() {

        producer?.close()
    }

    def "task events are projected into task_summary"() {

        given:
        def taskId = nextTaskId()

        when:
        send(event(taskId, TaskEventType.CREATED, "title", false, Instant.parse("2026-01-01T00:00:00Z")))
        send(event(taskId, TaskEventType.ARCHIVED, "title", true, Instant.parse("2026-01-01T00:00:01Z")))

        then:
        conditions.eventually {
            def summary = summary(taskId)
            assert summary?.archived == true
            assert summary.title == "title"
            assert summary.priority == 3
            assert summary.deleted == false
        }

        when:
        send(event(taskId, TaskEventType.DELETED, "title", true, Instant.parse("2026-01-01T00:00:02Z")))

        then:
        conditions.eventually {
            assert summary(taskId).deleted == true
        }
    }

    def "duplicated event is applied only once"() {

        given:
        def taskId = nextTaskId()
        def created = event(taskId, TaskEventType.CREATED, "original", false, Instant.now())
        send(created)
        conditions.eventually {
            assert summary(taskId)?.title == "original"
        }
        // 같은 이벤트가 다시 반영되면 title 이 original 로 돌아간다.
        jdbcTemplate.update("UPDATE task_summary SET title = 'changed' WHERE task_id = ?", taskId)

        when:
        send(created)

        then:
        verifyProcessed(created, 2)
        summary(taskId).title == "changed"
        jdbcTemplate.queryForObject("SELECT count(*) FROM processed_event WHERE event_id = ?::uuid", Integer,
            created.eventId) == 1
    }

    def "late event does not overwrite newer state"() {

        given:
        def taskId = nextTaskId()
        def archived = event(taskId, TaskEventType.ARCHIVED, "title", true, Instant.parse("2026-01-01T00:00:02Z"))
        def lateCreated = event(taskId, TaskEventType.CREATED, "title", false, Instant.parse("2026-01-01T00:00:01Z"))

        when: "archived event arrives before the created event"
        send(archived)
        send(lateCreated)

        then:
        conditions.eventually {
            assert jdbcTemplate.queryForObject("SELECT count(*) FROM processed_event WHERE event_id = ?::uuid", Integer,
                lateCreated.eventId) == 1
        }
        summary(taskId).archived == true
    }

    def "failed event is retried and then sent to the DLT"() {

        given:
        def taskId = nextTaskId()
        def failing = event(taskId, TaskEventType.CREATED, "failing", false, Instant.now())
        doThrow(new IllegalStateException("boom"))
            .when(taskEventUseCase).process(argThat { TaskEvent e -> e != null && e.eventId == failing.eventId })
        def dltConsumer = dltConsumer(KafkaAvroDeserializer)

        when:
        send(failing)
        def dltRecords = pollUntil(dltConsumer) { it.value()?.eventId == failing.eventId }

        then: "1 attempt + 3 retries"
        verifyProcessed(failing, 4)
        dltRecords.size() == 1
        header(dltRecords.first(), KafkaHeaders.DLT_EXCEPTION_MESSAGE).contains("boom")
        header(dltRecords.first(), KafkaHeaders.DLT_ORIGINAL_TOPIC) == TOPIC

        and: "next event on the topic is still processed"
        def nextTaskId = nextTaskId()
        send(event(nextTaskId, TaskEventType.CREATED, "next", false, Instant.now()))
        conditions.eventually {
            assert summary(nextTaskId)?.title == "next"
        }

        cleanup:
        dltConsumer?.close()
    }

    def "undeserializable record is sent to the DLT without retry"() {

        given:
        def rawProducer = new KafkaProducer<String, byte[]>([
            (ProducerConfig.BOOTSTRAP_SERVERS_CONFIG)     : kafka.bootstrapServers,
            (ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG)  : StringSerializer,
            (ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG): ByteArraySerializer,
        ] as Map<String, Object>)
        def key = "poison-${UUID.randomUUID()}".toString()
        def dltConsumer = dltConsumer(ByteArrayDeserializer)

        when:
        rawProducer.send(new ProducerRecord<>(TOPIC, key, "not-avro".bytes)).get()
        def dltRecords = pollUntil(dltConsumer) { it.key() == key }

        then:
        dltRecords.size() == 1
        new String(dltRecords.first().value() as byte[]) == "not-avro"
        header(dltRecords.first(), KafkaHeaders.DLT_EXCEPTION_FQCN).contains("DeserializationException")

        cleanup:
        rawProducer?.close()
        dltConsumer?.close()
    }

    // Mockito verify 는 boolean 을 반환해서 then 블록에 바로 쓰면 Spock 이 조건으로 평가하므로 void 메서드로 감싼다.
    private void verifyProcessed(TaskEvent event, int count) {

        verify(taskEventUseCase, timeout(10_000).times(count)).process(argThat { TaskEvent e -> e?.eventId == event.eventId })
    }

    private static long taskIdSequence = System.currentTimeMillis()

    private static long nextTaskId() {

        ++taskIdSequence
    }

    private static TaskEvent event(long taskId, TaskEventType type, String title, boolean archived, Instant occurredAt) {

        TaskEvent.newBuilder()
            .setEventId(UUID.randomUUID().toString())
            .setEventType(type)
            .setTaskId(taskId)
            .setTitle(title)
            .setContent("content")
            .setPriority(3)
            .setArchived(archived)
            .setOccurredAt(occurredAt)
            .build()
    }

    private void send(TaskEvent event) {

        producer.send(new ProducerRecord<>(TOPIC, String.valueOf(event.taskId), event)).get()
    }

    private Map<String, Object> summary(long taskId) {

        def rows = jdbcTemplate.queryForList("SELECT * FROM task_summary WHERE task_id = ?", taskId)
        rows ? rows.first() : null
    }

    private static KafkaConsumer dltConsumer(Class valueDeserializer) {

        def consumer = new KafkaConsumer([
            (ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG)                  : kafka.bootstrapServers,
            (ConsumerConfig.GROUP_ID_CONFIG)                           : "dlt-test-${UUID.randomUUID()}".toString(),
            (ConsumerConfig.AUTO_OFFSET_RESET_CONFIG)                  : "earliest",
            (ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG)             : StringDeserializer,
            (ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG)           : valueDeserializer,
            (AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG): schemaRegistryUrl(),
            (KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG)  : true,
        ] as Map<String, Object>)
        consumer.subscribe([DLT])
        consumer
    }

    private static List<ConsumerRecord> pollUntil(KafkaConsumer consumer, Closure<Boolean> filter) {

        def records = []
        def deadline = System.currentTimeMillis() + 30_000
        while (records.isEmpty() && System.currentTimeMillis() < deadline) {
            records.addAll(consumer.poll(Duration.ofMillis(500)).findAll(filter))
        }
        records
    }

    private static String header(ConsumerRecord record, String name) {

        def header = record.headers().lastHeader(name)
        header ? new String(header.value()) : null
    }
}
