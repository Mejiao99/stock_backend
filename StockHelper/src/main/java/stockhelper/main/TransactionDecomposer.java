package stockhelper.main;

import java.util.List;

public interface TransactionDecomposer {
    List<Transaction> decompose(List<InvestmentLine> from, List<InvestmentLine> to);
}
