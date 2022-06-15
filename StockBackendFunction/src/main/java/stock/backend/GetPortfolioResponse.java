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
public class GetPortfolioResponse {
    private List<PortfolioDefinition> portfolios;
    private Map<String, Double> conversionRates;
    private String targetCurrency;
    private Map<String,GetTableResponse> tablePerPortfolioDefinitions;
}
