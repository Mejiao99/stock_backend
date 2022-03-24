package stockhelper.main;

import java.util.List;

public interface PortfolioValueCalculator {
    double calculate(final List<InvestmentLine> lines, final String currency);
}

