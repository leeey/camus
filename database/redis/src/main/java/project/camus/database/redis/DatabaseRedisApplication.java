package project.camus.database.redis;

import io.lettuce.core.RedisClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class DatabaseRedisApplication {

    private final RedisClient redisClient;

    public static void main(String[] args) {

        SpringApplication.run(DatabaseRedisApplication.class, args);
    }
}
