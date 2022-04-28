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

    private Money convertCurrencyToTargetCurrency(final double conversionRate, final String targetCurrency) {
        return Money.builder().amount(amount * conversionRate).currency(targetCurrency).build();
    }

    private Money sum(final Money money) {
        final String moneyCurrency = money.currency;
        if (moneyCurrency.equals(currency)) {
            return Money.builder().amount(amount + money.amount).currency(moneyCurrency).build();
        }
        return money;
    }
}
