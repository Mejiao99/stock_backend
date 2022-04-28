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

    private Money moneyMultiply(final Money money, final double y) {
        return Money.builder().amount(money.amount * y).currency(money.getCurrency()).build();
    }

}
