package helloworld.Portfolio;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PortfolioRequester implements PortfoliosGenerator {

    @Override
    public List<Portfolio> getPortfolios(OperationRequest request) {
        List<Portfolio> portfolios = new ArrayList<>();

        if (request == OperationRequest.TEST) {
            Random rand = new Random();
            int quantity = rand.nextInt(10 - 1) + 1;
            String random = String.valueOf(rand.nextInt(50 - 1 + 1) + 1);
            for (int i = 0; i < quantity; i++) {
                Portfolio portfolio = Portfolio.builder()
                        .id(random)
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
