package stock.backend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YahooFinanceResponse {
    MarketYahoo marketYahoo = new MarketYahoo();
    private static final ObjectMapper objectMapper =
            new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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
        String sparkJson = callSparkApi(ticket);
        String quoteJson = callQuoteApi(ticket);
        Map<String, TicketInformation> ticketsInformation = getYahooFinanceSparkApiResponse(sparkJson);
        YahooFinanceApiResponse yahooQuoteApiResponse = getYahooQuoteApiResponse(quoteJson);
        return null;
    }

    private Map<String, TicketInformation> getYahooFinanceSparkApiResponse(String sparkJson) {
        try {
            return objectMapper.readValue(sparkJson, new TypeReference<Map<String, TicketInformation>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getTicketFromApiResponse(String ticket, YahooFinanceApiResponse response) {
        for (TicketInformation ticketInformation : response.quoteResponse.result) {
            if (ticketInformation.getSymbol().equals(ticket)) {
                return ticketInformation.getCurrency();
            }
        }
        return "";
    }

    private YahooFinanceApiResponse getYahooQuoteApiResponse(String quoteJson) {
        try {
            return objectMapper.readValue(quoteJson, YahooFinanceApiResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    private LocalDate localDateFromTimestamp(Integer timestampLong) {
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
