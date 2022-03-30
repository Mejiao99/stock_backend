package stock.backend;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class GetPortfolioResponse {
    List<PortfolioDefinition> portfolios;
    Map<String, Money> stockPrices;
    Map<String, Double> conversionRates;
    String targetCurrency;
}
