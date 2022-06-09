package stock.backend;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketRandom implements Market{
    @Override
    public Map<String, Money> calculateStockPrices(List<String> tickets, LocalDate date, String targetCurrency) {
        Map<String, Money> result = new HashMap<>();
        for (String ticket : tickets) {
            result.put(ticketSumDate(ticket, date), calculateTicketValue(ticket, date, targetCurrency));
        }
        return result;
    }
    private String ticketSumDate(String ticket, LocalDate date) {
        return ticket + "-" + date;
    }
    private Money calculateTicketValue(String ticket, LocalDate date, String targetCurrency) {
        double randomDouble = Math.random() * (20 - 1 + 1) + 1;
        return Money.builder().currency(targetCurrency).amount(randomDouble).build();
    }
}
