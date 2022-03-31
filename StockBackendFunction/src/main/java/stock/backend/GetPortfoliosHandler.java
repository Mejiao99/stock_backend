package stock.backend;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetPortfoliosHandler extends AbstractRequestHandler<GetPortfolioResponse> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected GetPortfolioResponse getResponse(APIGatewayProxyRequestEvent input, Context context) {
        Map<String, Double> holdingsC11 = new HashMap<>();
        holdingsC11.put("ticketA", 8.0);
        holdingsC11.put("ticketB", 5.0);
        holdingsC11.put("ticketC", 3.0);
        List<Account> accountsD1 = Collections.singletonList(Account.builder()
                .id("C1")
                .holdings(holdingsC11)
                .build());

        PortfolioDefinition portfolio1 = PortfolioDefinition.builder()
                .id("Xl1")
                .name("Port1")
                .accounts(accountsD1)
                .build();

        Map<String, Double> holdingsC22 = new HashMap<>();
        holdingsC22.put("ticketD", 7.0);
        holdingsC22.put("ticketE", 2.0);
        Map<String, Double> holdingsC23 = new HashMap<>();
        holdingsC23.put("ticketA", 4.0);
        holdingsC23.put("ticketD", 1.0);
        List<Account> accountsD2 = Arrays.asList(
                Account.builder()
                        .id("C1")
                        .holdings(holdingsC11)
                        .build(),
                Account.builder()
                        .id("C2")
                        .holdings(holdingsC22)
                        .build(),
                Account.builder()
                        .id("C3")
                        .holdings(holdingsC23)
                        .build());

        PortfolioDefinition portfolio2 = PortfolioDefinition.builder()
                .id("Xl2")
                .name("Port2")
                .accounts(accountsD2)
                .build();

        List<PortfolioDefinition> portfolios = Arrays.asList(portfolio1, portfolio2);

        Money priceA = Money.builder()
                .amount(5.0)
                .currency("usd")
                .build();
        Money priceB = Money.builder()
                .amount(2.0)
                .currency("usd")
                .build();
        Money priceC = Money.builder()
                .amount(7.0)
                .currency("usd")
                .build();
        Money priceD = Money.builder()
                .amount(1.0)
                .currency("cad")
                .build();
        Money priceE = Money.builder()
                .amount(9.0)
                .currency("cad")
                .build();
        Map<String, Money> stockPrices = new HashMap<>();
        stockPrices.put("ticketA", priceA);
        stockPrices.put("ticketB", priceB);
        stockPrices.put("ticketC", priceC);
        stockPrices.put("ticketD", priceD);
        stockPrices.put("ticketE", priceE);

        Map<String, Double> conversionRates = new HashMap<>();
        conversionRates.put("usd", 1.3);
        conversionRates.put("cad", 1.0);

        String targetCurrency = "cad";
        return GetPortfolioResponse.builder()
                .portfolios(portfolios)
                .stockPrices(stockPrices)
                .conversionRates(conversionRates)
                .targetCurrency(targetCurrency)
                .build();
    }

    private GetPortfolioResponse convertFromJson(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, GetPortfolioResponse.class);
    }
}
