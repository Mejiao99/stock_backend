package stockhelper.main;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PortfolioValueCalculatorPerDay {
    Map<LocalDate, Currency> calculate(final List<InvestmentLine> startingBalance, final List<Transaction> transactions,
                                       final LocalDate startDate, final LocalDate endDate);
}
