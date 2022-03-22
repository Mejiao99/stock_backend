package stock.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PortfolioProvider implements PortfoliosGenerator {

    @Override
    public List<Portfolio> getPortfolios(OperationRequest request) {
        List<Portfolio> portfolios = new ArrayList<>();

        if (request == OperationRequest.TEST) {
            Random rand = new Random();
            int quantity = rand.nextInt(10 - 1) + 1;

            for (int i = 0; i < quantity; i++) {
                String random = String.valueOf(rand.nextInt(50 - 1 + 1) + 1);
                Portfolio portfolio = Portfolio.builder()
                        .id(UUID.randomUUID().toString())
                        .name("portName " + random)
                        .accuracy(Math.random())
                        .totalHoldings(
                                Money.builder()
                                        .amount(1 + (50 - 1) * rand.nextDouble())
                                        .currency("USD")
                                        .build()
                        )
                        .build();
                portfolios.add(portfolio);
            }
        }
        return portfolios;
    }
}
