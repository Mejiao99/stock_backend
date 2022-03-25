package stock.backend;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import java.util.Collections;
import java.util.List;

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

        Money price = Money.builder()
                .amount(50.0)
                .currency("USD")
                .build();

        List<StockPrice> stockPrices = Collections.singletonList(StockPrice.builder()
                .ticket("ticketA")
                .price(price)
                .build());

        return GetPortfolioResponse.builder()
                .portfolios(portfolios)
                .stockPrices(stockPrices)
                .build();
    }
}
