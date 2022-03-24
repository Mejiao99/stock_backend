package stockhelper.main;


import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ToString
public class SingleAccountTransactionDecomposer implements TransactionDecomposer {
    // TODO: Make sure tickets are not repeated
    @Override
    public List<Transaction> decompose(final List<InvestmentLine> from, final List<InvestmentLine> to) {
        final Map<String, String> ticketToAccount = Stream.concat(from.stream(), to.stream())
                .collect(Collectors.toMap(
                                InvestmentLine::getTicket,
                                InvestmentLine::getAccount,
                                (account1, account2) -> account1
                        )
                );
        validateSameAccount(ticketToAccount.values());

        final Set<String> allTickets = Stream.concat(from.stream(), to.stream())
                .map(InvestmentLine::getTicket)
                .collect(Collectors.toSet());
        final Map<String, Integer> qtyFrom = getQuantityMap(from);
        final Map<String, Integer> qtyTo = getQuantityMap(to);
        final List<Transaction> result = new ArrayList<>();
        for (final String ticket : allTickets) {
            final String account = ticketToAccount.get(ticket);
            final int difference = qtyFrom.getOrDefault(ticket, 0) - qtyTo.getOrDefault(ticket, 0);
            if (difference > 0) {
                result.add(new Transaction(ticket, difference, account, TransactionOperation.SELL, null));
            } else if (difference < 0) {
                result.add(new Transaction(ticket, -difference, account, TransactionOperation.BUY, null));
            }
        }
        return result;
    }

    private void validateSameAccount(final Collection<String> accounts) {
        if (new HashSet<>(accounts).size() > 1) {
            throw new IllegalArgumentException("All accounts must be the same");
        }
    }

    private Map<String, Integer> getQuantityMap(List<InvestmentLine> investments) {
        return investments.stream()
                .collect(Collectors.toMap(InvestmentLine::getTicket, InvestmentLine::getQuantity));
    }
}
