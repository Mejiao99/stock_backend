package stock.backend;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GetPortfoliosHandler extends AbstractRequestHandler<GetPortfolioResponse> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected GetPortfolioResponse getResponse(APIGatewayProxyRequestEvent input, Context context) {
        final String contents = readContents();
        GetPortfolioResponse response = convertFromJson(contents);
        Map<String, GetTableResponse> tablePerPortfolioDefinitions = generateTablePerPortfolioDefinition(response);
        response.setTablePerPortfolioDefinitions(tablePerPortfolioDefinitions);
        return response;
    }

    private Map<String, GetTableResponse> generateTablePerPortfolioDefinition(GetPortfolioResponse getPortfolioResponse) {
        Map<String, GetTableResponse> response = new HashMap<>();
        for (PortfolioDefinition portfolioDefinition : getPortfolioResponse.getPortfolios()) {
            response.put(portfolioDefinition.getId(), GetTableResponse.builder()
                    .accounts(getAccounts(portfolioDefinition))
                    .tickets(getTickets(portfolioDefinition))
                    .data(generatePortfolioData(portfolioDefinition,getPortfolioResponse.getStockPrices()))
                    .totals(Totals.builder()
                            .accounts(Collections.emptyList())
                            .tickets(Collections.emptyList())
                            .total(Money.builder()
                                    .amount(0)
                                    .currency("")
                                    .build())
                            .build()).build());
        }
        return response;
    }

    private List<List<Money>> generatePortfolioData(PortfolioDefinition portfolioDefinition, Map<String, Money> stockPrices) {
        List<List<Money>> portfolioData = new ArrayList<>();
        for (Account account:portfolioDefinition.getAccounts()){
            portfolioData.add(calculateAccountHoldings(account.getHoldings(),stockPrices));
        }
        return portfolioData;
    }

    private List<Money> calculateAccountHoldings(Map<String, Double> holdings, Map<String, Money> stockPrices) {
        List<Money> accountMoney = new ArrayList<>();
        for (Map.Entry<String,Double> entry : holdings.entrySet()) {
            Money stockMoney = stockPrices.get(entry.getKey());
            accountMoney.add(Money.builder().currency(stockMoney.getCurrency()).amount(entry.getValue()*stockMoney.getAmount()).build());
        }
        return accountMoney;
    }

    private List<String> getTickets(PortfolioDefinition portfolioDefinition) {
        Set<String> ticketSet = new HashSet<>();
        for (Account account : portfolioDefinition.getAccounts()) {
            ticketSet.addAll(account.getHoldings().keySet());
        }
        return new ArrayList<>(ticketSet);
    }

    private List<String> getAccounts(PortfolioDefinition portfolioDefinition) {
        List<String> accountList = new ArrayList<>();
        for (Account account : portfolioDefinition.getAccounts()) {
            accountList.add(account.getId());
        }
        return accountList;
    }

    private String readContents() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("PortfolioDefinitions");

        Item row = table.getItem(new KeyAttribute("userId", "x@x.com"));

        String contents = row.getString("contents");
        return contents;
    }

    private GetPortfolioResponse convertFromJson(String json) {
        try {
            return objectMapper.readValue(json, GetPortfolioResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
