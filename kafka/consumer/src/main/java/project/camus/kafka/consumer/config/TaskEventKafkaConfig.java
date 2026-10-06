package project.camus.kafka.consumer.config;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
import org.springframework.kafka.core.MicrometerConsumerListener;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.RetryListener;
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
        KafkaProperties kafkaProperties, TaskEventKafkaProperties properties, MeterRegistry meterRegistry) {

        Map<String, Object> props = new LinkedHashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, properties.groupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, KafkaAvroDeserializer.class);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        DefaultKafkaConsumerFactory<String, TaskEvent> consumerFactory = new DefaultKafkaConsumerFactory<>(props);
        // kafka client 지표 (records-lag-max 등) 를 micrometer 에 등록한다.
        consumerFactory.addListener(new MicrometerConsumerListener<>(meterRegistry));

        ConcurrentKafkaListenerContainerFactory<String, TaskEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(properties.concurrency());
        factory.getContainerProperties().setAckMode(AckMode.RECORD);
        // kafka 헤더의 traceparent 를 이어받아 producer 와 같은 trace 로 처리한다.
        factory.getContainerProperties().setObservationEnabled(true);
        factory.setCommonErrorHandler(taskEventErrorHandler(kafkaProperties, properties, meterRegistry));
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

    private DefaultErrorHandler taskEventErrorHandler(KafkaProperties kafkaProperties, TaskEventKafkaProperties properties,
        MeterRegistry meterRegistry) {

        // 역직렬화에 실패한 레코드는 원본 byte[] 로, 처리에 실패한 레코드는 avro 로 DLT 에 보낸다.
        Map<Class<?>, KafkaOperations<?, ?>> templates = new LinkedHashMap<>();
        templates.put(byte[].class, dltTemplate(kafkaProperties, ByteArraySerializer.class));
        templates.put(Object.class, dltTemplate(kafkaProperties, KafkaAvroSerializer.class));

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(templates,
            (record, exception) -> new TopicPartition(properties.dltTopic(), record.partition()));

        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(properties.retry().maxRetries());
        backOff.setInitialInterval(properties.retry().initialInterval().toMillis());
        backOff.setMultiplier(properties.retry().multiplier());

        Counter deadLettered = Counter.builder("task.events.dead.letter")
            .description("task events sent to the DLT")
            .register(meterRegistry);
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
        errorHandler.setRetryListeners(new RetryListener() {

            @Override
            public void failedDelivery(ConsumerRecord<?, ?> record, Exception ex, int deliveryAttempt) {

            }

            @Override
            public void recovered(ConsumerRecord<?, ?> record, Exception ex) {

                deadLettered.increment();
            }
        });
        return errorHandler;
    }

    private KafkaTemplate<String, Object> dltTemplate(KafkaProperties kafkaProperties, Class<?> valueSerializer) {

        Map<String, Object> props = new LinkedHashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
    }
}
