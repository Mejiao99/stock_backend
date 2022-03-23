package stock.backend;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Portfolio {
    private String id;
    private String name;
    private double accuracy;
    private Money totalHoldings;
}
