package stock.backend;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class RandomPortfoliosProviderTest {

    @Test
    public void successfulRandomPortfoliosProvider() {
        RandomPortfoliosProvider portfoliosProvider = new RandomPortfoliosProvider();
        List<Portfolio> portfolios = portfoliosProvider.getPortfolios();
        assertNotNull(portfolios);

        for (Portfolio portfolio : portfolios) {
            assertFalse(portfolio.getId().isEmpty());
            assertFalse(portfolio.getName().isEmpty());
            assertTrue(accuracyCheck(portfolio.getAccuracy()));
            assertTrue(amountCheck(portfolio.getTotalHoldings().getAmount()));
            assertEquals("USD", portfolio.getTotalHoldings().getCurrency());
        }

    }

    private Boolean amountCheck(double amount) {
        if (amount > 1 && amount < 50) {
            return true;
        }
        return false;
    }

    private Boolean accuracyCheck(double amount) {
        if (amount > 0 && amount < 1) {
            return true;
        }
        return false;
    }
}