package stock_backend.Portfolio;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Money {
    private double amount;
    private String currency;
}
