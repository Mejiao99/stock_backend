package stock.backend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YahooFinanceResponse {
    MarketYahoo marketYahoo = new MarketYahoo();

    public static void main(String[] args) {
        YahooFinanceResponse response = new YahooFinanceResponse();
    }

    private String callSparkApi(String symbols) {
        try {
            return Files.readString(Paths.get("C:\\Users\\Jonathan\\Documents\\HelloWorldFunction\\StockBackendFunction\\src\\main\\java\\stock\\backend\\apispark.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String callQuoteApi(String symbols) {
        try {
            return Files.readString(Paths.get("C:\\Users\\Jonathan\\Documents\\HelloWorldFunction\\StockBackendFunction\\src\\main\\java\\stock\\backend\\apiquote.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<LocalDate, Money> getStockHistoricalPrice(String ticket) {
        return null;
    }

    private long calculateInterval(LocalDate startDate, LocalDate endDate) {
        return endDate.getDayOfMonth() - startDate.getDayOfMonth();
    }

    private List<Timestamp> datesToTimestamps(List<LocalDate> localDates) {
        List<Timestamp> result = new ArrayList<>();
        for (LocalDate localDate : localDates) {
            result.add(toTimestamp(localDate));
        }
        return result;
    }

    private LocalDate localDateFromTimestampLong(Integer timestampLong) {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestampLong), ZoneId.of("UTC")).toLocalDate();
    }

    private Timestamp toTimestamp(LocalDate localDate) {
        return Timestamp.valueOf(localDate.atStartOfDay());
    }

    private List<LocalDate> datesInRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> result = startDate.datesUntil(endDate).collect(Collectors.toList());
        result.add(endDate);
        return result;
    }
}
