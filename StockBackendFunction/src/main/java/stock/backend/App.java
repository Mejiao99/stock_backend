package stock.backend;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        headers.put("Access-Control-Allow-Headers", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "OPTIONS,POST,GET");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
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

            Money money = Money.builder()
                    .amount(50.0)
                    .currency("USD")
                    .build();

            List<StockPrice> stockPrices = Collections.singletonList(StockPrice.builder()
                    .ticket("ticketA")
                    .money(money)
                    .build());

            GetPortfolioResponse portfolioResponse = GetPortfolioResponse.builder()
                    .portfolios(portfolios)
                    .stockPrices(stockPrices)
                    .build();


            final String output = objectMapper.writeValueAsString(portfolioResponse);

            return response
                    .withStatusCode(200)
                    .withBody(output);
        } catch (IOException e) {
            return response
                    .withBody("{}")
                    .withStatusCode(500);
        }
    }

}
