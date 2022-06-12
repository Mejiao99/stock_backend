package stock.backend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        Map<String, Money> map = market.calculateStockPrices(tickets);
        System.err.println(map);
    }

    @Override
    public Map<String, Money> calculateStockPrices(List<String> tickets) {
        String symbols = formatTicketListToCallApi(tickets);
        String quoteJson = callQuoteApi(symbols);

        YahooFinanceApiResponse yahooQuoteApiResponse = getYahooQuoteApiResponse(quoteJson);
        List<TicketInformation> ticketsInformation = yahooQuoteApiResponse.getQuoteResponse().getResult();

        Map<String, Money> result = new HashMap<>();
        for (TicketInformation ticketInformation : ticketsInformation) {
            String ticket = ticketInformation.getSymbol();
            result.put(ticket, ticketInformationToMoney(ticketInformation));
        }
        return result;
    }

    public Money ticketInformationToMoney(TicketInformation ticketInformation) {
        final String currency = ticketInformation.getCurrency();
        double amount = ticketInformation.getRegularMarketPreviousClose();
        return Money.builder().amount(amount).currency(currency).build();
    }

    public String formatTicketListToCallApi(List<String> tickets) {
        StringBuilder result = new StringBuilder();
        for (String ticket : tickets) {
            result.append(ticket).append("%2C");
        }
        return String.valueOf(result);
    }

    private String callSparkApi(String interval, String range, String symbols) {
        try {
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create("https://yfapi.net/v6/finance/quote?region=US&lang=en" + "&symbols=" + symbols))
//                    .header("x-api-key", System.getenv("YAHOO_API"))
//                    .method("GET", HttpRequest.BodyPublishers.noBody())
//                    .build();
//            HttpResponse<String> response = HttpClient.newHttpClient()
//                    .send(request, HttpResponse.BodyHandlers.ofString());
//            return response.body();
            return Files.readString(Paths.get("C:\\Users\\Jonathan\\Documents\\HelloWorldFunction\\StockBackendFunction\\src\\main\\java\\stock\\backend\\apispark.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String callQuoteApiExchange(String fromCurrency, String toCurrency) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://yfapi.net/v6/finance/quote?region=US&lang=en" + "&symbols=" + fromCurrency + toCurrency + "%3DX"))
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

    private String callQuoteApi(String symbols) {
        try {
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create("https://yfapi.net/v8/finance/spark?interval=" + interval + "&range=" + range + "&symbols=" + symbols))
//                    .header("x-api-key", System.getenv("YAHOO_API"))
//                    .method("GET", HttpRequest.BodyPublishers.noBody())
//                    .build();
//            HttpResponse<String> response = HttpClient.newHttpClient()
//                    .send(request, HttpResponse.BodyHandlers.ofString());
//            return response.body();
            return Files.readString(Paths.get("C:\\Users\\Jonathan\\Documents\\HelloWorldFunction\\StockBackendFunction\\src\\main\\java\\stock\\backend\\apiquote.json"));
        } catch (IOException e) {
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

    private Map<String, TicketInformation> getYahooFinanceSparkApiResponse(String sparkJson) {
        try {
            return objectMapper.readValue(sparkJson, new TypeReference<Map<String, TicketInformation>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
