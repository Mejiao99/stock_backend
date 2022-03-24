package stockhelper.main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PortfolioValueCalculatorImplTest {
    private Market market;
    private PortfolioValueCalculatorImpl valueCalculator;

    List<InvestmentLine> investments = Arrays.asList(
            new InvestmentLine("A", 78, "x"),
            new InvestmentLine("B", 100, "x"));

    List<InvestmentLine> mixedInvestments = Arrays.asList(
            new InvestmentLine("A", 78, "x"),
            new InvestmentLine("B", 100, "x"),
            new InvestmentLine("X", 44, "x"),
            new InvestmentLine("Y", 78, "x"));


    @BeforeEach
    public void setup() {
        market = mock(Market.class);
        valueCalculator = new PortfolioValueCalculatorImpl(market);

        when(market.getStockValue("A")).thenReturn(new Currency(10.0, "USD"));
        when(market.getStockValue("B")).thenReturn(new Currency(20.0, "USD"));

        when(market.getStockValue("X")).thenReturn(new Currency(7.0, "CAD"));
        when(market.getStockValue("Y")).thenReturn(new Currency(9.0, "CAD"));

        when(market.exchangeRate("USD", "CAD")).thenReturn(1.5);
        when(market.exchangeRate("USD", "USD")).thenReturn(1.0);

        when(market.exchangeRate("CAD", "USD")).thenReturn(0.5);
        when(market.exchangeRate("CAD", "CAD")).thenReturn(1.0);
    }

    //TODO: 4 acciones de ellas 2 en usd  2 en cad y valor
    @Test
    public void when_sum_all_stock_value_from_only_usd_portfolio_returns_expected() {
        // Preparation

        // Execution
        double valueUSD = valueCalculator.calculate(investments, "USD");

        // Validations
        assertEquals(2780, valueUSD);
    }
    @Test
    public void when_sum_all_stock_value_from_only_cad_portfolio_returns_expected() {
        // Preparation

        // Execution
        double valueCAD = valueCalculator.calculate(investments, "CAD");

        // Validations
        assertEquals(4170, valueCAD);
    }
    @Test
    public void when_sum_all_stock_value_from_mixed_currency_portfolio_returns_expected() {
        // Preparation
        // stock_a_value: 780, stock_b_value:2000, stock_x_value_in_usd:154,stock_y_value_in_usd:351

        // Execution
        double valueUSD = valueCalculator.calculate(mixedInvestments, "USD");

        // Validations
        // all_value_stock_in_usd = 3285
        assertEquals(3285, valueUSD);
    }
}
