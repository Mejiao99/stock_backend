package stock.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GetPortfoliosHandlerTest {
    private GetPortfoliosHandler handler;
    List<Money> moneyList = new ArrayList<>();

    @BeforeEach
    public void setup() {
        handler = new GetPortfoliosHandler();

        moneyList.add(Money.builder().amount(4).currency("usd").build());
        moneyList.add(Money.builder().amount(3).currency("usd").build());
        moneyList.add(Money.builder().amount(5).currency("cad").build());
    }
    @Test
    void classifyMoneyPerCurrency() {
        Map<String, Money> moneyPerCurrency = handler.classifyMoneyPerCurrency(moneyList);
        assertNotNull(moneyPerCurrency);
        assertEquals(moneyPerCurrency.size() , 2);
        assertEquals(moneyPerCurrency.get("usd") , validateMoney());
        assertEquals(moneyPerCurrency.get("cad") , 7);
    }
    private void validateMoney(Money money, double amount, String currency) {
        assertEquals(amount, money.getAmount());
        assertEquals(currency, money.getCurrency());

    }

}