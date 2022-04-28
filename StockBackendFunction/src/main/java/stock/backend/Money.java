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

    private double multiplyDoubles(double x, double y) {
        return x * y;
    }

    private double convertAmount(double amount, double conversionRatio) {
        return multiplyDoubles(amount, conversionRatio);
    }

    private double sumDoubles(double x, double y) {
        return x + y;
    }


}
