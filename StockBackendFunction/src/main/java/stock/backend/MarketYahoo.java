package stock.backend;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
public class MarketYahoo implements Market{
    private static final ObjectMapper objectMapper =
            new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    @Override
    public Map<String, Money> calculateStockPrices(List<String> tickets, LocalDate date, String targetCurrency) {
        System.err.println("anything");
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("https://yfapi.net/v8/finance/chart/AAPL"))
//                .header("x-api-key", "YOUR-PERSONAL-API-KEY")
//                .method("GET", HttpRequest.BodyPublishers.noBody())
//                .build();
//        HttpResponse<String> response = HttpClient.newHttpClient()
//                .send(request, HttpResponse.BodyHandlers.ofString());
        String json = callApi(tickets);
       System.err.println(json);
        System.err.println(getTicketInformation(json));
//        System.out.println(response.body());
        return null;
    }

    private String callApi(List<String> tickets) {
        try {
            return Files.readString(Paths.get("C:\\Users\\Jonathan\\Documents\\HelloWorldFunction\\StockBackendFunction\\src\\main\\java\\stock\\backend\\yahooxic.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TicketInformation getTicketInformation (String json){
        try{
            return objectMapper.readValue(json, TicketInformation.class);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Market market = new MarketYahoo();
        Map<String, Money> map = market.calculateStockPrices(null, null, null);
        System.err.println(map);
    }
}
