package stock.backend;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockPrice {
    String ticket;
    Money money;
}
