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
            List<String> accountTickets = getTickets(portfolioDefinition);
            response.put(portfolioDefinition.getId(), GetTableResponse.builder()
                    .accounts(getAccounts(portfolioDefinition))
                    .tickets(accountTickets)
                    .data(generatePortfolioData(portfolioDefinition, getPortfolioResponse.getStockPrices(), accountTickets))
                    .totals(calculateTotals(portfolioDefinition, getPortfolioResponse, accountTickets)).build());
        }
        return response;
    }

    private Totals calculateTotals(PortfolioDefinition portfolioDefinition, GetPortfolioResponse getPortfolioResponse, List<String> accountTickets) {
        return Totals.builder()
                .accounts(accountTotals(
                        portfolioDefinition,
                        getPortfolioResponse.getStockPrices(),
                        getPortfolioResponse.getConversionRates(),
                        getPortfolioResponse.getTargetCurrency(),
                        accountTickets))
                .tickets(ticketsTotals(
                        portfolioDefinition,
                        getPortfolioResponse.getStockPrices(),
                        getPortfolioResponse.getConversionRates(),
                        getPortfolioResponse.getTargetCurrency(),
                        accountTickets
                ))
                .total(Money.builder()
                        .amount(0)
                        .currency("")
                        .build())
                .build();
    }

    private Map<String, Money> totalPerCurrency(Map<String, Money> portfolioStocks, List<String> currencyList) {
        Map<String, Money> totalPerCurrency = new HashMap<>();
        for (String currency : currencyList) {
            totalPerCurrency.put(currency, sumListMoney(stockPerCurrencyList(portfolioStocks, currency), currency));
        }
        return totalPerCurrency;
    }

    private Money sumListMoney(List<Money> monies, String currency) {
        double total = 0.0;
        for (Money money : monies) {
            total = total + money.getAmount();
        }
        return Money.builder().amount(total).currency(currency).build();
    }

    private List<Money> stockPerCurrencyList(Map<String, Money> portfolioStocks, String currency) {
        List<Money> result = new ArrayList<>();
        for (Map.Entry<String, Money> currentMoney : portfolioStocks.entrySet()) {
            if (currentMoney.getValue().getCurrency().equals(currency)) {
                result.add(currentMoney.getValue());
            }
        }
        return result;
    }

    private List<Money> ticketsTotals(PortfolioDefinition portfolioDefinition, Map<String, Money> stockPrices, Map<String, Double> conversionRates, String targetCurrency, List<String> tickets) {
        List<Money> ticketsTotals = new ArrayList<>();

        return ticketsTotals;
    }

    private List<Money> accountTotals(PortfolioDefinition portfolioDefinition, Map<String, Money> stockPrices, Map<String, Double> conversionRates, String targetCurrency, List<String> tickets) {
        List<Money> accountTotals = new ArrayList<>();
        for (Account account : portfolioDefinition.getAccounts()) {
            List<Money> accountHoldings = calculateAccountHoldings(account.getHoldings(), stockPrices, tickets);
            accountTotals.add(calculateAccountTotal(accountHoldings, conversionRates, targetCurrency));
        }
        return accountTotals;
    }

    private Money calculateAccountTotal(List<Money> accountHoldings, Map<String, Double> conversionRates, String currencyTarget) {
        double total = 0.0;
        for (Money money : accountHoldings) {
            if (money.getCurrency().equals(currencyTarget)) {
                total = total + money.getAmount();
            } else {
                total = total + money.getAmount() * conversionRates.get(money.getCurrency());
            }
        }
        return Money.builder().amount(total).currency(currencyTarget).build();
    }

    private List<List<Money>> generatePortfolioData(PortfolioDefinition portfolioDefinition, Map<String, Money> stockPrices, List<String> accountTickets) {
        List<List<Money>> portfolioData = new ArrayList<>();
        for (Account account : portfolioDefinition.getAccounts()) {
            portfolioData.add(calculateAccountHoldings(account.getHoldings(), stockPrices, accountTickets));
        }
        return portfolioData;
    }

    private List<Money> calculateAccountHoldings(Map<String, Double> holdings, Map<String, Money> stockPrices, List<String> accountTickets) {
        List<Money> accountMoney = new ArrayList<>();
        for (String ticket : accountTickets) {
            accountMoney.add(calculateTotalAmountTicket(ticket, stockPrices, holdings));
        }
        return accountMoney;
    }

    private Money calculateTotalAmountTicket(String ticket, Map<String, Money> stockPrices, Map<String, Double> holdings) {
        Double accountHoldingAmount = holdings.get(ticket);
        Money stockMoney = stockPrices.get(ticket);
        if (accountHoldingAmount == null) {
            return Money.builder().amount(0.0).currency(stockMoney.getCurrency()).build();
        }
        return Money.builder().amount(accountHoldingAmount * stockMoney.getAmount()).currency(stockMoney.getCurrency()).build();
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
