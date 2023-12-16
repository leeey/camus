package project.camus.jwt.webmvc.util;

import static com.fasterxml.jackson.dataformat.csv.CsvSchema.builder;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType;
import java.io.IOException;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import project.camus.common.exception.CamusServerException;
import project.camus.common.util.ResourceUtil;
import project.camus.jwt.webmvc.api.dto.JwtRefreshTokenDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtRefreshTokenUtil {

    private static final String PATH = "jwt/refresh-token-list.csv";

    public static List<JwtRefreshTokenDto> getRefreshTokens() {

        CsvMapper csvMapper = new CsvMapper();
        try (MappingIterator<JwtRefreshTokenDto> mappingIterator = csvMapper
            .readerFor(JwtRefreshTokenDto.class)
            .with(csvSchema())
            .readValues(ResourceUtil.getPathResource(PATH).getFile())) {

            return mappingIterator.readAll();
        } catch (IOException e) {
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
