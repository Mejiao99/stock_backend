package stock.backend;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GetMarketHandler extends AbstractRequestHandler<GetMarketResponse> implements Market {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final GetPortfoliosHandler getPortfoliosHandler = new GetPortfoliosHandler();


    @Override
    protected GetMarketResponse getResponse(APIGatewayProxyRequestEvent input, Context context) {
        List<String> tickets = new ArrayList<>(getTickets(getPortfoliosHandler.getResponse(input, context)));
        Map<String, Money> stockPrices = calculateStockPrices(tickets, LocalDate.now(), "CAD");
        List<Item> itemList = makeItemsFromStock(stockPrices);
        System.err.println(itemList);
        putItems(itemList);

        return GetMarketResponse.builder().data("hello world!").build();
    }

    private Set<String> getTickets(GetPortfolioResponse getPortfolioResponse) {
        Set<String> tickets = new HashSet<>();
        for (PortfolioDefinition portfolioDefinition : getPortfolioResponse.getPortfolios()) {
            tickets.addAll(portfolioDefinition.getTargetHoldings().keySet());
            for (Account account : portfolioDefinition.getAccounts()) {
                tickets.addAll(account.getHoldings().keySet());
            }
        }
        return tickets;
    }

    private Item makeItem(String ticket, Money money) {
        String output = null;
        try {
            output = objectMapper.writeValueAsString(money);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new Item()
                .withPrimaryKey("ticketDate", ticket)
                .withString("value", output);
    }

    private List<Item> makeItemsFromStock(Map<String, Money> stockPrices) {
        List<Item> result = new ArrayList<>();
        for (Map.Entry<String, Money> stock : stockPrices.entrySet()) {
            result.add(makeItem(stock.getKey(), stock.getValue()));
        }
        return result;
    }

    private void putItemsInTable(List<Item> items) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);
        TableWriteItems writeItems = new TableWriteItems("StockMarket").withItemsToPut(items);
        dynamoDB.batchWriteItem(writeItems);
    }

    private void putItems(List<Item> items) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("StockMarket");
        for (Item item: items){
            table.putItem(item);
        }
    }

    @Override
    public Map<String, Money> calculateStockPrices(List<String> tickets, LocalDate date, String targetCurrency) {
        Map<String, Money> result = new HashMap<>();
        for (String ticket : tickets) {
            result.put(ticketSumDate(ticket, date), calculateTicketValue(ticket, date, targetCurrency));
        }
        return result;
    }

    private String ticketSumDate(String ticket, LocalDate date) {
        return ticket + "-" + date;
    }

    private Money calculateTicketValue(String ticket, LocalDate date, String targetCurrency) {
        double randomDouble = Math.random() * (20 - 1 + 1) + 1;
        return Money.builder().currency(targetCurrency).amount(randomDouble).build();
    }
}
