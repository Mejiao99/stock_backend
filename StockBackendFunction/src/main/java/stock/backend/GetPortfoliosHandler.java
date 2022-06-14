package stock.backend;

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
    private final MarketYahoo market = new MarketYahoo();

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
            System.err.println("AccountTickets: " + accountTickets);
            Map<String, Money> stockPrices = market.calculateStockPrices(accountTickets);
            System.err.println("stockPrices: " + stockPrices);
            response.put(portfolioDefinition.getId(), GetTableResponse.builder()
                    .accounts(getAccounts(portfolioDefinition))
                    .tickets(accountTickets)
                    .data(generatePortfolioData(portfolioDefinition,stockPrices , accountTickets))
                    .totals(calculateTotals(portfolioDefinition, getPortfolioResponse, accountTickets,stockPrices)).build());
        }
        return response;
    }

    // This method returns the totals of the portfolio
    private Totals calculateTotals(PortfolioDefinition portfolioDefinition, GetPortfolioResponse getPortfolioResponse, List<String> accountTickets,Map<String,Money> stockPrices) {
        List<Money> accountTotals = accountTotals(
                portfolioDefinition,
                stockPrices,
                getPortfolioResponse.getConversionRates(),
                getPortfolioResponse.getTargetCurrency(),
                accountTickets);
        return Totals.builder()
                .accounts(accountTotals)
                .tickets(ticketsTotals(portfolioDefinition.getAccounts(), stockPrices))
                .total(calculateTotalHoldingPortfolio(accountTotals, getPortfolioResponse.getTargetCurrency()))
                .build();
    }

    // This method returns the total holding in the portfolio
    private Money calculateTotalHoldingPortfolio(List<Money> accountTotals, String targetCurrency) {
        Money total = Money.builder().amount(0).currency(targetCurrency).build();
        for (Money money : accountTotals) {
            total = total.sum(money);
        }
        return total;
    }

    // This method returns a map with key ticketName and totalMoney per currency in the portfolio
    public Map<String, Money> classifyMoneyPerCurrency(Map<String, Money> totalMoneyPerTicket) {
        Map<String, Money> moneyPerCurrency = new HashMap<>();
        List<Money> moneyList = new ArrayList<>(totalMoneyPerTicket.values());
        moneyList.forEach(money -> moneyPerCurrency.merge(money.getCurrency(), money, (oldValue, newValue) -> oldValue.sum(money)));
        return moneyPerCurrency;
    }

    // This method returns a map with key ticketName and totalMoney of the ticket in the portfolio
    public Map<String, Money> classifyMoneyPerTicket(List<Account> accounts, Map<String, Money> stockPrices) {
        Map<String, Money> moneyPerTicket = new HashMap<>();
        Map<String, Double> totalTicketsQtyInPortfolio = totalTicketsQtyInPortfolio(accounts);
        totalTicketsQtyInPortfolio.entrySet().forEach(stringDoubleEntry ->
                moneyPerTicket.computeIfAbsent(
                        stringDoubleEntry.getKey(), k ->
                                Money.builder()
                                        .currency(stockPrices.get(stringDoubleEntry.getKey()).getCurrency())
                                        .amount(stockPrices.get(stringDoubleEntry.getKey()).getAmount() * stringDoubleEntry.getValue())
                                        .build()));
        return moneyPerTicket;
    }

    // this method returns a map with key ticketName and totalQty of the ticket in the portfolio
    public Map<String, Double> totalTicketsQtyInPortfolio(List<Account> accounts) {
        Map<String, Double> totalAmountTickets = new HashMap<>();
        for (Account account : accounts) {
            for (Map.Entry<String, Double> entry : account.getHoldings().entrySet()) {
                totalAmountTickets.merge(entry.getKey(), entry.getValue(), Double::sum);
            }
        }
        return totalAmountTickets;
    }

    // This method returns a list with the total amount per ticket.
    private List<Money> ticketsTotals(List<Account> accounts, Map<String, Money> stockPrices) {
        List<Money> ticketsTotals = new ArrayList<>();
        final Map<String, Money> moneyPerTicket = classifyMoneyPerTicket(accounts, stockPrices);
        ticketsTotals.addAll(moneyPerTicket.values());
        return ticketsTotals;
    }

    // This method returns a list with totals per existing account the portfolioDefinition
    private List<Money> accountTotals(PortfolioDefinition portfolioDefinition, Map<String, Money> stockPrices, Map<String, Double> conversionRates, String targetCurrency, List<String> tickets) {
        List<Money> accountTotals = new ArrayList<>();
        for (Account account : portfolioDefinition.getAccounts()) {
            List<Money> accountHoldings = calculateAccountHoldings(account.getHoldings(), stockPrices, tickets);
            accountTotals.add(calculateAccountTotal(accountHoldings, conversionRates, targetCurrency));
        }
        return accountTotals;
    }

    // This method calculate the total amount given an accountHolding in the targetCurrency
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

    // This method returns a matrix with the existing money in the portfolio accounts.
    private List<List<Money>> generatePortfolioData(PortfolioDefinition portfolioDefinition, Map<String, Money> stockPrices, List<String> accountTickets) {
        List<List<Money>> portfolioData = new ArrayList<>();
        for (Account account : portfolioDefinition.getAccounts()) {
            portfolioData.add(calculateAccountHoldings(account.getHoldings(), stockPrices, accountTickets));
        }
        return portfolioData;
    }

    // This method calculate the total amount of holding given an account
    private List<Money> calculateAccountHoldings(Map<String, Double> holdings, Map<String, Money> stockPrices, List<String> accountTickets) {
        List<Money> accountMoney = new ArrayList<>();
        for (String ticket : accountTickets) {
            accountMoney.add(calculateTotalAmountTicket(ticket, stockPrices, holdings));
        }
        return accountMoney;
    }

    // This method calculate the total amount given a ticket.
    private Money calculateTotalAmountTicket(String ticket, Map<String, Money> stockPrices, Map<String, Double> holdings) {
        Double accountHoldingAmount = holdings.get(ticket);
        if (stockPrices.get(ticket) == null) {
            System.err.println("TicketNull= "+ticket);
            return Money.builder().amount(0.0).currency("").build();
        }
        Money stockMoney = stockPrices.get(ticket);
        System.err.println("stockMoney: "+stockMoney);
        if (accountHoldingAmount == null) {
            return Money.builder().amount(0.0).currency(stockMoney.getCurrency()).build();
        }
        return Money.builder().amount(accountHoldingAmount * stockMoney.getAmount()).currency(stockMoney.getCurrency()).build();
    }

    // This method returns all ticket names existing in a portfolioDefinition without duplicates
    private List<String> getTickets(PortfolioDefinition portfolioDefinition) {
        Set<String> ticketSet = new HashSet<>();
        for (Account account : portfolioDefinition.getAccounts()) {
            ticketSet.addAll(account.getHoldings().keySet());
        }
        return new ArrayList<>(ticketSet);
    }

    // This method returns all existing account id in a portfolioDefinition
    private List<String> getAccounts(PortfolioDefinition portfolioDefinition) {
        List<String> accountList = new ArrayList<>();
        for (Account account : portfolioDefinition.getAccounts()) {
            accountList.add(account.getId());
        }
        return accountList;
    }

    private String readContents() {
        Table table = GetMarketHandler.DYNAMO_DB.getTable("PortfolioDefinitions");

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
