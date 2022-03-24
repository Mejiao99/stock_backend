package stockhelper.main;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class AssetsBracketsCalculatorImpl implements AssetsBracketsCalculator {
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("PST", ZoneId.SHORT_IDS);

    @Override
    public Map<LocalDate, List<InvestmentLine>> calculate(List<Transaction> transactions) {
        Map<LocalDate, List<Transaction>> transactionsPerDay = new HashMap<>();
        for (Transaction transaction : transactions) {
            transactionsPerDay.computeIfAbsent(LocalDate.ofInstant(transaction.getDate(), DEFAULT_ZONE_ID), __ -> new ArrayList<>())
                    .add(transaction);
        }

        List<LocalDate> allDates = new ArrayList<>(transactionsPerDay.keySet());
        Collections.sort(allDates);

        Map<LocalDate, List<InvestmentLine>> portfolioPerDay = new HashMap<>();
        for (LocalDate date : allDates) {
            Map<Key, Integer> totalPerAccountTicket = new HashMap<>();
            for (Transaction transaction : transactionsPerDay.get(date)) {
                final int qty;
                if (transaction.getOperation() == TransactionOperation.BUY) {
                    qty = transaction.getQuantity();
                } else {
                    qty = -transaction.getQuantity();
                }
                totalPerAccountTicket.merge(
                        new Key(transaction.getAccount(), transaction.getTicket()),
                        qty,
                        Integer::sum);
            }

            List<InvestmentLine> lines = new ArrayList<>();
            for (Map.Entry<Key, Integer> entry : totalPerAccountTicket.entrySet()) {
                String ticket = entry.getKey().getTicket();
                int quantity = entry.getValue();
                String account = entry.getKey().getAccount();
                InvestmentLine line = new InvestmentLine(ticket, quantity, account);
                lines.add(line);
            }
            portfolioPerDay.put(date, lines);
        }
        Map<LocalDate, List<InvestmentLine>> result = new HashMap<>();
        List<InvestmentLine> accumulate = new ArrayList<>();
        for (LocalDate date : allDates) {
            List<InvestmentLine> currentLines = accumulate;
            accumulate = reduce(currentLines, portfolioPerDay.get(date));
            result.put(date, accumulate);
        }
        return result;
    }

    private List<InvestmentLine> reduce(List<InvestmentLine> listA, List<InvestmentLine> listB) {
        if (listA == null || listA.isEmpty()) {
            return listB;
        }
        List<InvestmentLine> result = new ArrayList<>();
        for (InvestmentLine lineA : listA) {
            for (InvestmentLine lineB : listB) {
                result.add(lineSum(lineB, lineA));
            }
        }
        return result;
    }


    private InvestmentLine lineSum(InvestmentLine lineA, InvestmentLine lineB) {
        if (lineA.getTicket().equals(lineB.getTicket()) && lineA.getAccount().equals(lineB.getAccount())) {
            if (lineA.getQuantity() == lineB.getQuantity()) {
                return lineA;
            }
            int total = lineA.getQuantity() + lineB.getQuantity();
            return new InvestmentLine(lineA.getTicket(), total, lineA.getAccount());
        }
        return null;
    }

    private InvestmentLine findLine(List<InvestmentLine> lines, String ticket, String account) {
        if (lines == null) {
            return null;
        }
        for (InvestmentLine line : lines) {
            String fromTicket = line.getTicket();
            String fromAccount = line.getTicket();
            if (fromTicket.equals(ticket) && fromAccount.equals(account)) {
                return line;
            }
        }
        return null;
    }

    @Data
    @AllArgsConstructor
    static class Key {
        private String account;
        private String ticket;

    }

}
