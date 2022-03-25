package stock.backend;

import lombok.Builder;

import java.util.List;
@Builder
public class GetPortfolioResponse {
    List<PortfolioDefinition> portfolios;
    List <StockPrice> stockPrices;
}
