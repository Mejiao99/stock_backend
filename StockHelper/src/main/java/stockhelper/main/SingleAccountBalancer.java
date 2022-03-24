package stockhelper.main;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@AllArgsConstructor
public class SingleAccountBalancer implements PortfolioBalancer {
    private Market market;
    private PortfolioValueCalculatorImpl calculator;

    @Override
    public List<InvestmentLine> balance(List<InvestmentLine> currentItems, Map<String, Double> allocations) {

        if (currentItems == null || currentItems.isEmpty()) {
            return Collections.emptyList();
        }

        if (allocations == null || allocations.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> accounts = getAllAccounts(currentItems);
        if (accounts.size() != 1) {
            throw new DivergeAccountsException(accounts);
        }
        String account = accounts.stream().findFirst().get();

        double totalValue = calculator.calculate(currentItems, "USD");

        Map<String, Integer> results = new HashMap<>();
        for (Map.Entry<String, Double> entry : allocations.entrySet()) {

            String ticket = entry.getKey();
            double allocation = entry.getValue();

            Currency ticketCurrency = market.getStockValue(ticket);
            double conversionRate = market.exchangeRate(ticketCurrency.getSymbol(), "USD");

            double ticketAmount = ticketCurrency.getAmount();
            double priceUSD = ticketAmount * conversionRate;
            int stockQty = (int) ((totalValue * allocation) / priceUSD);
            results.put(ticket, stockQty);
        }

        List<InvestmentLine> balanceInvestmentList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            InvestmentLine balanceStock = new InvestmentLine(entry.getKey(), entry.getValue(), account);
            balanceInvestmentList.add(balanceStock);
        }
        return balanceInvestmentList;
    }

    private Set<String> getAllAccounts(List<InvestmentLine> currentItems) {
        Set<String> accounts = new HashSet<>();
        for (InvestmentLine line : currentItems) {
            accounts.add(line.getAccount());
        }
        return accounts;
    }
}
