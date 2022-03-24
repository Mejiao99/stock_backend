package stockhelper.main;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@ToString
public class PerAccountTransactionDecomposer {
    private SingleAccountTransactionDecomposer decomposer;

    public List<Transaction> decompose(final List<InvestmentLine> from, final List<InvestmentLine> to) {
        final Set<String> allAccounts = Stream.concat(from.stream(), to.stream())
                .map(InvestmentLine::getAccount)
                .collect(Collectors.toSet());

        final List<Transaction> result = new ArrayList<>();
        for (final String account : allAccounts) {

            List<InvestmentLine> fromAccountTransactions = filterAccount(from, account);
            List<InvestmentLine> toAccountTransactions = filterAccount(to, account);

            List<Transaction> transactions = decomposer.decompose(fromAccountTransactions, toAccountTransactions);

            result.addAll(transactions);
        }
        return result;
    }

    private List<InvestmentLine> filterAccount(final List<InvestmentLine> investments, final String account) {
        return investments.stream().
                filter(investmentLine -> investmentLine.getAccount().equals(account)).
                collect(Collectors.toList());
    }

}
