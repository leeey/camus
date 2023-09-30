package leeey.project.lotto.util;

import static com.fasterxml.jackson.dataformat.csv.CsvSchema.builder;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType;
import java.io.IOException;
import java.util.List;
import leeey.project.lotto.dto.HistoryDto;
import leeey.project.lotto.exception.LottoException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HistoryUtil {

  private static final String HISTORY_FILE_PATH = "history.csv";

  public static List<HistoryDto> getHistories() {

    CsvMapper csvMapper = new CsvMapper();
    try (MappingIterator<HistoryDto> mappingIterator = csvMapper
        .readerFor(HistoryDto.class)
        .with(csvSchema())
        .readValues(ResourceUtil.getPathResource(HISTORY_FILE_PATH).getFile())) {

      return mappingIterator.readAll();
    } catch (IOException e) {
      throw new LottoException(e);
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
