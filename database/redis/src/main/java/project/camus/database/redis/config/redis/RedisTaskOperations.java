package project.camus.database.redis.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializationContext.RedisSerializationContextBuilder;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import project.camus.database.redis.model.task.Task;

@Configuration
public class RedisTaskOperations {

    @Bean
    public ReactiveRedisOperations<String, Task> taskOperations(ReactiveRedisConnectionFactory factory) {

        JacksonJsonRedisSerializer<Task> serializer = new JacksonJsonRedisSerializer<>(Task.class);

        RedisSerializationContextBuilder<String, Task> builder =
            RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, Task> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
