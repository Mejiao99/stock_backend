package stock.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetPortfoliosHandlerTest {
    private GetPortfoliosHandler handler;
    private Map<String, Money> portfolioStocks = new HashMap<>();
    private List<String> currencyList = new ArrayList<>();

    @BeforeEach
    public void setup() {
        handler = new GetPortfoliosHandler();
        // Preparation
        portfolioStocks.put("ticketA", Money.builder().amount(4).currency("usd").build());
        portfolioStocks.put("ticketB", Money.builder().amount(3).currency("usd").build());
        portfolioStocks.put("ticketC", Money.builder().amount(5).currency("cad").build());
        currencyList.add("usd");
        currencyList.add("cad");
        currencyList.add("eur");

    }

    @Test
    public void moneyListPerCurrency() {
        List<Money> moneyListPerCurrency = handler.perCurrencyList(portfolioStocks, "usd");
        assertEquals(2, moneyListPerCurrency.size());
        assertEquals("usd", moneyListPerCurrency.get(0).getCurrency());
        assertEquals(4, moneyListPerCurrency.get(0).getAmount());
        assertEquals(3, moneyListPerCurrency.get(1).getAmount());
    }

    @Test
    public void totalPerCurrency() {
        List<Money> totalPerCurrencyList = handler.perCurrencyTotalList(portfolioStocks, currencyList);
        assertEquals(3, totalPerCurrencyList.size());
        validateMoney(find(totalPerCurrencyList, "usd"), 7, "usd");
        validateMoney(find(totalPerCurrencyList, "cad"), 5, "cad");
        validateMoney(find(totalPerCurrencyList, "eur"), 0, "eur");
    }

    private Money find(List<Money> moneyList, String currency) {
        for (Money money : moneyList) {
            if (money.getCurrency().equals(currency)) {
                return money;
            }
        }
        return null;
    }

    private void validateMoney(Money money, double amount, String currency) {
        assertEquals(amount, money.getAmount());
        assertEquals(currency, money.getCurrency());

    }
}