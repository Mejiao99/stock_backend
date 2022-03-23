package stock.backend;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RandomPortfoliosProviderTest {

    @Test
    public void successfulRandomPortfoliosProvider() {
        RandomPortfoliosProvider portfoliosProvider = new RandomPortfoliosProvider();
        List<Portfolio> portfolios = portfoliosProvider.getPortfolios();
        assertNotNull(portfolios);

        for (Portfolio portfolio : portfolios) {
            assertFalse(portfolio.getId().isEmpty());
            assertFalse(portfolio.getName().isEmpty());

            assertThat(portfolio.getAccuracy(), allOf(
                    greaterThan(0.0),
                    lessThan(1.0)));
            assertThat(portfolio.getTotalHoldings().getAmount(), allOf(
                    greaterThan(1.0),
                    lessThan(50.0)));

            assertEquals("USD", portfolio.getTotalHoldings().getCurrency());
        }

    }


}