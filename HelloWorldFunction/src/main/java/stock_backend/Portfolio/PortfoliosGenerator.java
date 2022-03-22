package stock_backend.Portfolio;

import stock_backend.Api.OperationRequest;

import java.util.List;


public interface PortfoliosGenerator {
    List<Portfolio> getPortfolios(OperationRequest request);
}
