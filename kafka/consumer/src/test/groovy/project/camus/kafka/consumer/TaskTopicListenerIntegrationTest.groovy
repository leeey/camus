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
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import project.camus.kafka.avro.Task
import project.camus.kafka.consumer.listener.dto.TaskDto
import project.camus.kafka.consumer.usecase.TaskUseCase

class TaskTopicListenerIntegrationTest extends IntegrationTestSupport {

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
