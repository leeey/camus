package project.camus.webflux.app.lotto.util;

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
import project.camus.webflux.app.lotto.dto.LottoHistoryDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LottoHistoryUtil {

    private static final String PATH = "lotto/history.csv";

    public static List<LottoHistoryDto> getHistories() {

        CsvMapper csvMapper = new CsvMapper();
        try (MappingIterator<LottoHistoryDto> mappingIterator = csvMapper
            .readerFor(LottoHistoryDto.class)
            .with(csvSchema())
            .readValues(ResourceUtil.getPathResource(PATH).getFile())) {

            return mappingIterator.readAll();
        } catch (IOException e) {
            throw new CamusServerException(e);
        }
    }

    private static CsvSchema csvSchema() {
        return builder()
            .addColumn("round", ColumnType.STRING)
            .addColumn("date", ColumnType.STRING)
            .addColumn("no1", ColumnType.NUMBER)
            .addColumn("no2", ColumnType.NUMBER)
            .addColumn("no3", ColumnType.NUMBER)
            .addColumn("no4", ColumnType.NUMBER)
            .addColumn("no5", ColumnType.NUMBER)
            .addColumn("no6", ColumnType.NUMBER)
            .addColumn("noBonus", ColumnType.NUMBER)
            .build().withHeader();
    }
}
