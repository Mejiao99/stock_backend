package stockhelper.main;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AssetsBracketsCalculator {
    Map<LocalDate, List<InvestmentLine>> calculate (List<Transaction> transactions);
}
