package project.camus.kafka.consumer.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import project.camus.event.task.TaskEvent;

/**
 * task-events 수신 설정.
 * <p>
 * 처리에 실패하면 지수 백오프로 재시도한 뒤 task-events.DLT 로 보낸다. 역직렬화 실패처럼 재시도해도 소용없는 오류는 바로 DLT 로 보낸다.
 */
@Configuration
@EnableConfigurationProperties(TaskEventKafkaProperties.class)
public class TaskEventKafkaConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaskEvent> taskEventListenerContainerFactory(
        KafkaProperties kafkaProperties, TaskEventKafkaProperties properties) {

        Map<String, Object> props = new LinkedHashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, properties.groupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, KafkaAvroDeserializer.class);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        ConcurrentKafkaListenerContainerFactory<String, TaskEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props));
        factory.setConcurrency(properties.concurrency());
        factory.getContainerProperties().setAckMode(AckMode.RECORD);
        factory.setCommonErrorHandler(taskEventErrorHandler(kafkaProperties, properties));
        return factory;
    }

    @Bean
    public NewTopic taskEventsDltTopic(TaskEventKafkaProperties properties) {

        // DLT 는 원본과 같은 파티션 번호로 보내므로 원본 이상의 파티션이 필요하다.
        return TopicBuilder.name(properties.dltTopic())
            .partitions(properties.dltPartitions())
            .replicas(properties.dltReplicas())
            .build();
    }

    private DefaultErrorHandler taskEventErrorHandler(KafkaProperties kafkaProperties, TaskEventKafkaProperties properties) {

        // 역직렬화에 실패한 레코드는 원본 byte[] 로, 처리에 실패한 레코드는 avro 로 DLT 에 보낸다.
        Map<Class<?>, KafkaOperations<?, ?>> templates = new LinkedHashMap<>();
        templates.put(byte[].class, dltTemplate(kafkaProperties, ByteArraySerializer.class));
        templates.put(Object.class, dltTemplate(kafkaProperties, KafkaAvroSerializer.class));

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(templates,
            (record, exception) -> new TopicPartition(properties.dltTopic(), record.partition()));

        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(properties.retry().maxRetries());
        backOff.setInitialInterval(properties.retry().initialInterval().toMillis());
        backOff.setMultiplier(properties.retry().multiplier());

        return new DefaultErrorHandler(recoverer, backOff);
    }

    private KafkaTemplate<String, Object> dltTemplate(KafkaProperties kafkaProperties, Class<?> valueSerializer) {

        Map<String, Object> props = new LinkedHashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
    }
}
