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
    private static final ObjectMapper objectMapper =
            new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static void main(String[] args) {
        YahooFinanceResponse response = new YahooFinanceResponse();
        System.err.println(response.localDateFromTimestamp(1652673600));
        System.err.println(response.getStockHistoricalPrice("XUU.TO"));
    }

    private String callSparkApi(String symbols) {
        try {
            return Files.readString(Paths.get("C:\\Users\\Jonathan\\Documents\\HelloWorldFunction\\StockBackendFunction\\src\\main\\java\\stock\\backend\\singleSparkMax.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String callQuoteApi(String symbols) {
        try {
            return Files.readString(Paths.get("C:\\Users\\Jonathan\\Documents\\HelloWorldFunction\\StockBackendFunction\\src\\main\\java\\stock\\backend\\singleQuote.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<LocalDate, Money> getStockHistoricalPrice(String ticket) {
        String sparkJson = callSparkApi(ticket);
        String quoteJson = callQuoteApi(ticket);
        Map<String, TicketInformation> ticketsInformation = getYahooFinanceSparkApiResponse(sparkJson);
        YahooFinanceApiResponse yahooQuoteApiResponse = getYahooQuoteApiResponse(quoteJson);

        Map<LocalDate, Money> result = new HashMap<>();
        for (Map.Entry<String, TicketInformation> entry : ticketsInformation.entrySet()) {
            TicketInformation ticketInformation = entry.getValue();
            for (Integer timeStamp:ticketInformation.getTimestamp()){
                int LocationOfClose = ticketInformation.getTimestamp().indexOf(timeStamp);
                Double amount = ticketInformation.getClose().get(LocationOfClose);
                result.put(localDateFromTimestamp(timeStamp),ticketInformationToMoney(ticketInformation,ticket,amount,yahooQuoteApiResponse));
            }
        }
        return result;
    }

    public Money ticketInformationToMoney(TicketInformation ticketInformation, String ticket,Double amount, YahooFinanceApiResponse response) {
        final String currency = getTicketFromApiResponse(ticket, response);
        return Money.builder().amount(amount).currency(currency).build();
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

    private LocalDate localDateFromTimestamp(Integer timestamp) {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.of("UTC")).toLocalDate();
    }

    private Timestamp toTimestamp(LocalDate localDate) {
        return Timestamp.valueOf(localDate.atStartOfDay());
    }

    private List<LocalDate> datesInRange(LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate).collect(Collectors.toList());
    }
}