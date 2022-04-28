package stock.backend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Money {
    private double amount;
    private String currency;

    private Money multiply(final double factor) {
        return Money.builder().amount(amount * factor).currency(currency).build();
    }

}
