package stock.backend;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class GetPortfolioResponse {
    private List<PortfolioDefinition> portfolios;
    private List<StockPrice> stockPrices;
}
