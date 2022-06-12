package stock.backend;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface Market {
     Map<String, Money> calculateStockPrices(List<String> tickets);
}
