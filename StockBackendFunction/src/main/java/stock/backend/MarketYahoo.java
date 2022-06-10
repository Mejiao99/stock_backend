package stock.backend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketYahoo implements Market {
    private static final ObjectMapper objectMapper =
            new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static void main(String[] args) {
        Market market = new MarketYahoo();
        List<String> tickets = new ArrayList<>();
        tickets.add("XAW.TO");
        tickets.add("XUU.TO");
        Map<String, Money> map = market.calculateStockPrices(tickets, null, null);
    }

    @Override
    public Map<String, Money> calculateStockPrices(List<String> tickets, LocalDate date, String targetCurrency) {
        String symbols = formatTicketListToCallApi(tickets);
        String json = callApi("1d", "1wk", symbols);
        Map<String, TicketInformation> ticketsInformation = getYahooFinanceSparkApiResponse(json);
        Map<String, Money> result = new HashMap<>();
        for (Map.Entry<String, TicketInformation> entry : ticketsInformation.entrySet()) {
            result.put(ticketSumDate(entry.getKey(),date), ticketInformationToMoney(entry.getValue()));
        }
        return result;
    }

    public Money ticketInformationToMoney(TicketInformation ticketInformation) {
        return Money.builder().amount(ticketInformation.getChartPreviousClose()).currency("CAD").build();
    }

    public String formatTicketListToCallApi(List<String> tickets) {
        StringBuilder result = new StringBuilder();
        for (String ticket : tickets) {
            result.append(ticket).append("%2C");
        }
        return String.valueOf(result);
    }

    private String callApi(String interval, String range, String symbols) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://yfapi.net/v8/finance/spark?interval=" + interval + "&range=" + range + "&symbols=" + symbols))
                    .header("x-api-key", System.getenv("YAHOO_API"))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private Map<String, TicketInformation> getYahooFinanceSparkApiResponse(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, TicketInformation>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String ticketSumDate(String ticket, LocalDate date) {
        return ticket + "-" + date;
    }
}
