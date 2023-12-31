package project.camus.database.redis.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializationContext.RedisSerializationContextBuilder;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import project.camus.database.redis.model.user.User;

@Configuration
public class RedisUserOperations {

    @Bean
    public ReactiveRedisOperations<String, User> userOperations(ReactiveRedisConnectionFactory factory) {

        Jackson2JsonRedisSerializer<User> serializer = new Jackson2JsonRedisSerializer<>(User.class);

        RedisSerializationContextBuilder<String, User> builder =
            RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, User> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
