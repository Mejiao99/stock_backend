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

    public Money convertCurrencyToTargetCurrency(final double conversionRate, final String targetCurrency) {
        return Money.builder().amount(amount * conversionRate).currency(targetCurrency).build();
    }

    public Money sum(final Money other) {
        final String moneyCurrency = other.currency;
        if (!moneyCurrency.equals(currency)) {
            throw new RuntimeException();
        }
        return Money.builder().amount(amount + other.amount).currency(moneyCurrency).build();
    }

}


