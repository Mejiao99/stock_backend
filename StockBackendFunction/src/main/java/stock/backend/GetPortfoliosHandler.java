package stock.backend;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import java.util.Collections;

public class GetPortfoliosHandler extends AbstractRequestHandler<GetPortfolioResponse> {

    @Override
    protected GetPortfolioResponse getResponse(APIGatewayProxyRequestEvent input, Context context) {
        return GetPortfolioResponse.builder()
                .portfolios(Collections.emptyList())
                .stockPrices(Collections.emptyList())
                .build();
    }
}
