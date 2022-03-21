package helloworld.Portfolio;

import java.util.List;


public interface PortfoliosGenerator {
    List<Portfolio> getPortfolios(OperationRequest request);
}
