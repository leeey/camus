package project.camus.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;
import project.camus.common.exception.CamusServerException;

@Configuration
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectMapperUtil {

    public static ObjectMapper getInstance() {
        return ObjectMapperLazyHolder.INSTANCE;
    }

    private static class ObjectMapperLazyHolder {

        private static final ObjectMapper INSTANCE = getMapper();

        private static ObjectMapper getMapper() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);

            return objectMapper;
        }
    }

    public static <T> String toJson(T t) {

        try {
            return getInstance().writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw new CamusServerException(e);
        }
    }
}
