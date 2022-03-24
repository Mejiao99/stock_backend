package stockhelper.main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PerAccountBalancerTest {
    private Market market;
    private PerAccountBalancer balancer;
    private PortfolioValueCalculatorImpl calculator;

    @BeforeEach
    public void setup() {
        market = mock(Market.class);
        calculator = new PortfolioValueCalculatorImpl(market);
        balancer = new PerAccountBalancer(new SingleAccountBalancer(market, calculator));
        when(market.getStockValue("A")).thenReturn(new Currency(10.0, "USD"));
        when(market.getStockValue("B")).thenReturn(new Currency(20.0, "USD"));
        when(market.getStockValue("C")).thenReturn(new Currency(5.0, "USD"));
        when(market.getStockValue("D")).thenReturn(new Currency(15.0, "USD"));

        when(market.getStockValue("X")).thenReturn(new Currency(7.0, "CAD"));
        when(market.getStockValue("Y")).thenReturn(new Currency(9.0, "CAD"));
        when(market.getStockValue("Z")).thenReturn(new Currency(12.0, "CAD"));

        when(market.exchangeRate("USD", "CAD")).thenReturn(1.265822784810127);
        when(market.exchangeRate("USD", "USD")).thenReturn(1.0);

        when(market.exchangeRate("CAD", "USD")).thenReturn(0.79);
        when(market.exchangeRate("CAD", "CAD")).thenReturn(1.0);
    }

    @Test
    public void one_to_one_per_account() {
        // Preparation
        InvestmentLine c1StockA = new InvestmentLine("A", 100, "c1");
        InvestmentLine c2StockA = new InvestmentLine("A", 20, "c2");

        Map<String, Double> allocations = new HashMap<>();
        allocations.put("B", 1.0);

        // Execution
        List<InvestmentLine> newAllocations = balancer.balance(Arrays.asList(c1StockA, c2StockA), allocations);

        // Validations:
        assertNotNull(newAllocations);
        assertEquals(2, newAllocations.size());
        validateInvestmentLine(find(newAllocations, "B", "c1"), "B", 50, "c1");
        validateInvestmentLine(find(newAllocations, "B", "c2"), "B", 10, "c2");
    }

    @Test
    public void six_to_six_per_account() {
        // Preparation
        InvestmentLine c1StockA = new InvestmentLine("A", 51, "c1");
        InvestmentLine c1StockB = new InvestmentLine("B", 20, "c1");
        InvestmentLine c1StockC = new InvestmentLine("C", 60, "c1");
        InvestmentLine c1StockD = new InvestmentLine("D", 25, "c1");
        InvestmentLine c1StockX = new InvestmentLine("X", 75, "c1");
        InvestmentLine c1StockY = new InvestmentLine("Y", 10, "c1");

        InvestmentLine c2StockA = new InvestmentLine("A", 50, "c2");
        InvestmentLine c2StockB = new InvestmentLine("B", 150, "c2");
        InvestmentLine c2StockC = new InvestmentLine("C", 95, "c2");
        InvestmentLine c2StockD = new InvestmentLine("D", 40, "c2");
        InvestmentLine c2StockX = new InvestmentLine("X", 80, "c2");
        InvestmentLine c2StockY = new InvestmentLine("Y", 200, "c2");

        Map<String, Double> allocations = new HashMap<>();
        allocations.put("A", 0.20);
        allocations.put("B", 0.5);
        allocations.put("C", 0.5);
        allocations.put("D", 0.10);
        allocations.put("X", 0.30);
        allocations.put("Y", 0.30);

        // Execution
        List<InvestmentLine> newAllocations = balancer.balance(Arrays.asList(c1StockA, c1StockB, c1StockC, c1StockD, c1StockX, c1StockY
                        , c2StockA, c2StockB, c2StockC, c2StockD, c2StockX, c2StockY),
                allocations);

        // Validations:
        assertNotNull(newAllocations);
        assertEquals(12, newAllocations.size());
        System.out.println(newAllocations);
        validateInvestmentLine(find(newAllocations, "A", "c1"), "A", 41, "c1");
        validateInvestmentLine(find(newAllocations, "B", "c1"), "B", 51, "c1");
        validateInvestmentLine(find(newAllocations, "C", "c1"), "C", 207, "c1");
        validateInvestmentLine(find(newAllocations, "D", "c1"), "D", 13, "c1");
        validateInvestmentLine(find(newAllocations, "X", "c1"), "X", 112, "c1");
        validateInvestmentLine(find(newAllocations, "Y", "c1"), "Y", 87, "c1");

        validateInvestmentLine(find(newAllocations, "A", "c2"), "A", 128, "c2");
        validateInvestmentLine(find(newAllocations, "B", "c2"), "B", 160, "c2");
        validateInvestmentLine(find(newAllocations, "C", "c2"), "C", 643, "c2");
        validateInvestmentLine(find(newAllocations, "D", "c2"), "D", 42, "c2");
        validateInvestmentLine(find(newAllocations, "X", "c2"), "X", 349, "c2");
        validateInvestmentLine(find(newAllocations, "Y", "c2"), "Y", 271, "c2");
    }

    private void validateInvestmentLine(InvestmentLine investmentLine, String ticket, int quantity, String account) {
        assertEquals(ticket, investmentLine.getTicket());
        assertEquals(quantity, investmentLine.getQuantity());
        assertEquals(account, investmentLine.getAccount());
    }

    private InvestmentLine find(List<InvestmentLine> newAllocations, String ticket, String account) {
        for (InvestmentLine stock : newAllocations) {
            if (stock.getTicket().equals(ticket) && stock.getAccount().equals(account)) {
                return stock;
            }
        }
        return null;
    }


}
