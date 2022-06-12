package stock.backend;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketRandom implements Market{
    @Override
    public Map<String, Money> calculateStockPrices(List<String> tickets) {
        Map<String, Money> result = new HashMap<>();
        for (String ticket : tickets) {
            result.put(ticket, calculateTicketValue());
        }
        return result;
    }

    private Money calculateTicketValue() {
        double randomDouble = Math.random() * (20 - 1 + 1) + 1;
        return Money.builder().currency("USD").amount(randomDouble).build();
    }
}
