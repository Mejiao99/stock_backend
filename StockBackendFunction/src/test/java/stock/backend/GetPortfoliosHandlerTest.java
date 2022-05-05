package stock.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GetPortfoliosHandlerTest {
    List<Money> moneyList = new ArrayList<>();
    Map<String, Money> stockPrices = new HashMap<>();
    List<Account> accounts = new ArrayList<>();
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

        Map<String, Double> c1Holdings = new HashMap<>();
        c1Holdings.put("TicketA", 2.0);
        c1Holdings.put("TicketB", 5.0);
        c1Holdings.put("TicketC", 7.0);
        Map<String, Double> c2Holdings = new HashMap<>();
        c2Holdings.put("TicketA", 2.0);
        c2Holdings.put("TicketB", 5.0);
        c2Holdings.put("TicketC", 9.0);
        c2Holdings.put("TicketE", 2.0);

        accounts.add(Account.builder().holdings(c1Holdings).build());
        accounts.add(Account.builder().holdings(c2Holdings).build());

        stockPrices.put("TicketA", Money.builder().amount(1).currency("usd").build());
        stockPrices.put("TicketB", Money.builder().amount(4).currency("usd").build());
        stockPrices.put("TicketC", Money.builder().amount(2).currency("usd").build());
        stockPrices.put("TicketE", Money.builder().amount(9).currency("cad").build());

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

    @Test
    void totalAmountTickets() {
        Map<String, Double> totalAmountTickets = handler.totalAmountTickets(accounts);
        assertNotNull(totalAmountTickets);
        assertEquals(totalAmountTickets.size(), 4);
        assertEquals(totalAmountTickets.get("TicketA"), 4);
        assertEquals(totalAmountTickets.get("TicketB"), 10);
        assertEquals(totalAmountTickets.get("TicketC"), 16);
        assertEquals(totalAmountTickets.get("TicketE"), 2);

    }

    @Test
    void classifyMoneyPerTicket() {
        Map<String, Money> classifyMoneyPerTicket = handler.classifyMoneyPerTicket(accounts, stockPrices);
        assertNotNull(classifyMoneyPerTicket);
        assertEquals(classifyMoneyPerTicket.size(), 4);
        validateMoney(classifyMoneyPerTicket.get("TicketA"),4,"usd");
        validateMoney(classifyMoneyPerTicket.get("TicketB"),40,"usd");
        validateMoney(classifyMoneyPerTicket.get("TicketC"),32,"usd");
        validateMoney(classifyMoneyPerTicket.get("TicketE"),18,"cad");

    }

    private void validateMoney(Money money, double amount, String currency) {
        assertEquals(amount, money.getAmount());
        assertEquals(currency, money.getCurrency());

    }

}