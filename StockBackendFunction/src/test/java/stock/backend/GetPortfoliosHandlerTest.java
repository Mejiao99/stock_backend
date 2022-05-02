package stock.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GetPortfoliosHandlerTest {
    List<Money> moneyList = new ArrayList<>();
    private GetPortfoliosHandler handler;

    @BeforeEach
    public void setup() {
        handler = new GetPortfoliosHandler();

        moneyList.add(Money.builder().amount(8).currency("usd").build());
        moneyList.add(Money.builder().amount(2).currency("usd").build());
        moneyList.add(Money.builder().amount(4).currency("usd").build());

        moneyList.add(Money.builder().amount(1).currency("cad").build());
        moneyList.add(Money.builder().amount(9).currency("cad").build());
        moneyList.add(Money.builder().amount(7).currency("cad").build());
        moneyList.add(Money.builder().amount(3).currency("cad").build());

        moneyList.add(Money.builder().amount(5).currency("eur").build());
    }

    @Test
    void classifyMoneyPerCurrency() {
        Map<String, Money> moneyPerCurrency = handler.classifyMoneyPerCurrency(moneyList);

        assertNotNull(moneyPerCurrency);
        assertEquals(moneyPerCurrency.size(), 3);

        validateMoney(moneyPerCurrency.get("usd"), 14, "usd");
        validateMoney(moneyPerCurrency.get("cad"), 20, "cad");
        validateMoney(moneyPerCurrency.get("eur"), 5, "eur");
    }

    private void validateMoney(Money money, double amount, String currency) {
        assertEquals(amount, money.getAmount());
        assertEquals(currency, money.getCurrency());

    }

}