package project.camus.common.util;

import java.io.InputStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import project.camus.common.exception.CamusServerException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectMapperUtil {

    public static ObjectMapper getInstance() {

        return ObjectMapperLazyHolder.INSTANCE;
    }

    private static class ObjectMapperLazyHolder {

        private static final ObjectMapper INSTANCE = getMapper();

        private static ObjectMapper getMapper() {

            return JsonMapper.builder()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID)
                .build();
        }
    }

    public static <T> String toJson(T t) {

        try {
            return getInstance().writeValueAsString(t);
        } catch (JacksonException e) {
            throw new CamusServerException(e);
        }
    }

    public static <T> T convertValue(Object value, Class<T> clazz) {

        try {
            return getInstance().convertValue(value, clazz);
        } catch (Exception e) {
            throw new CamusServerException(e);
        }
    }

    public static <T> T readInputStreamValue(InputStream input) {

        try {
            return getInstance().readValue(input, new TypeReference<>() {
            });
        } catch (JacksonException e) {
            throw new CamusServerException(e);
        }
    }

    public static <T> T readString(String value) {

        try {
            return getInstance().readValue(value, new TypeReference<>() {
            });
        } catch (JacksonException e) {
            throw new CamusServerException(e);
        }
    }

    public static byte[] writeValueAsBytes(Object value) {

        try {
            return getInstance().writeValueAsBytes(value);
        } catch (JacksonException e) {
            throw new CamusServerException(e);
        }
    }
}
