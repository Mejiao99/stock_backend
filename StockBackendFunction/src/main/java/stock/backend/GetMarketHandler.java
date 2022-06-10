package stock.backend;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GetMarketHandler extends AbstractRequestHandler<GetMarketResponse>  {
    public static final AmazonDynamoDB CLIENT = AmazonDynamoDBClientBuilder.standard()
            .build();
    public static final DynamoDB DYNAMO_DB = new DynamoDB(CLIENT);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final GetPortfoliosHandler getPortfoliosHandler = new GetPortfoliosHandler();


    @Override
    protected GetMarketResponse getResponse(APIGatewayProxyRequestEvent input, Context context) {
//        List<String> tickets = new ArrayList<>(getTickets(getPortfoliosHandler.getResponse(input, context)));
//        Map<String, Money> stockPrices = new MarketRandom().calculateStockPrices(tickets, LocalDate.now(), "CAD");
        List<String> tickets = new ArrayList<>();
        tickets.add("XAW.TO");
        tickets.add("XUU.TO");
        Map<String, Money> stockPrices = new MarketYahoo().calculateStockPrices(tickets, LocalDate.now(), "CAD");
        List<Item> itemList = makeItemsFromStock(stockPrices);
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

    private void putItems(List<Item> items) {
        Table table = DYNAMO_DB.getTable("StockMarket");
        for (Item item: items){
            table.putItem(item);
        }
    }

}

