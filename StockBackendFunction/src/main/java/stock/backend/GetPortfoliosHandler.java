package stock.backend;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetPortfoliosHandler extends AbstractRequestHandler<GetPortfolioResponse> {

    @Override
    protected GetPortfolioResponse getResponse(APIGatewayProxyRequestEvent input, Context context) {
        List<Holding> holdings = Collections.singletonList(Holding.builder()
                .quantity(1)
                .ticket("ticketA")
                .build());

        List<Account> accounts = Collections.singletonList(Account.builder()
                .id("C1")
                .holdings(holdings)
                .build());

        PortfolioDefinition portfolio = PortfolioDefinition.builder()
                .id("Xl1")
                .name("Port1")
                .accounts(accounts)
                .build();

        List<PortfolioDefinition> portfolios = Collections.singletonList(portfolio);

        Money priceA = Money.builder()
                .amount(5)
                .currency("USD")
                .build();
        Map<String,Money> stockPrices = new HashMap<>();
        stockPrices.put("ticketA",priceA);

        return GetPortfolioResponse.builder()
                .portfolios(portfolios)
                .stockPrices(stockPrices)
                .build();
    }
}
