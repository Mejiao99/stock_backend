package stock.backend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDefinition {
    private String id;
    private String name;
    private List<Account> accounts;
    private Map<String, Double> targetHoldings;
}
