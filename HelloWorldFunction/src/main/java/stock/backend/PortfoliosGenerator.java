package stock.backend;

import java.util.List;


public interface PortfoliosGenerator {
    List<Portfolio> getPortfolios(OperationRequest request);
}
