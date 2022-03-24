package stockhelper.main;

import java.util.List;
import java.util.Map;

public interface PortfolioBalancer {
    List<InvestmentLine> balance(List<InvestmentLine> currentItems, Map<String, Double> allocations);
}
