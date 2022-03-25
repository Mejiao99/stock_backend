package stock.backend;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetPortfolioResponse {
    List<PortfolioDefinition> portfolios;
    List<StockPrice> stockPrices;
}
