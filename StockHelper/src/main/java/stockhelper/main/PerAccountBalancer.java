package stockhelper.main;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public class PerAccountBalancer implements PortfolioBalancer {
    private SingleAccountBalancer singleAccountBalancer;

    @Override
    public List<InvestmentLine> balance(List<InvestmentLine> currentItems, Map<String, Double> allocations) {
        List<String> accounts = getAllAccounts(currentItems);

        List<InvestmentLine> result = new ArrayList<>();
        for (String account : accounts) {
            // Find all account actions
            List<InvestmentLine> accountLines = filterAccount(currentItems, account);
            // Balance account
            List<InvestmentLine> balancedLines = singleAccountBalancer.balance(accountLines, allocations);
            // Balanced accounts add to result
            result.addAll(balancedLines);
        }
        return result;
    }

    private List<String> getAllAccounts(List<InvestmentLine> currentItems) {
        Set<String> accounts = new HashSet<>();
        for (InvestmentLine line : currentItems) {
            accounts.add(line.getAccount());
        }
        return new ArrayList<>(accounts);
    }

    private List<InvestmentLine> filterAccount(List<InvestmentLine> currentItems, String account) {
        List<InvestmentLine> accountList = new ArrayList<>();
        for (InvestmentLine line : currentItems) {
            if (line.getAccount().equals(account)) {
                accountList.add(line);
            }
        }
        return accountList;
    }
}
