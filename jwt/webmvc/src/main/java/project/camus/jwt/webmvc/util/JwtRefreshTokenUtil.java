package project.camus.jwt.webmvc.util;

import static tools.jackson.dataformat.csv.CsvSchema.builder;

import java.io.IOException;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import project.camus.common.exception.CamusServerException;
import project.camus.common.util.ResourceUtil;
import project.camus.jwt.webmvc.api.dto.JwtRefreshTokenDto;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.MappingIterator;
import tools.jackson.dataformat.csv.CsvMapper;
import tools.jackson.dataformat.csv.CsvSchema;
import tools.jackson.dataformat.csv.CsvSchema.ColumnType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtRefreshTokenUtil {

    private static final String PATH = "jwt/refresh-token-list.csv";

    public static List<JwtRefreshTokenDto> getRefreshTokens() {

        CsvMapper csvMapper = new CsvMapper();
        try (MappingIterator<JwtRefreshTokenDto> mappingIterator = csvMapper
            .readerFor(JwtRefreshTokenDto.class)
            .with(csvSchema())
            .readValues(ResourceUtil.getPathResource(PATH).getInputStream())) {

            return mappingIterator.readAll();
        } catch (IOException | JacksonException e) {
            throw new CamusServerException(e);
        }
    }

    private static CsvSchema csvSchema() {

        return builder()
            .addColumn("token", ColumnType.STRING)
            .addColumn("expiredAt", ColumnType.STRING)
            .build().withHeader();
    }
}
