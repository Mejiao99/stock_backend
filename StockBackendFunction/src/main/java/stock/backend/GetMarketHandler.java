package stock.backend;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

public class GetMarketHandler extends AbstractRequestHandler<GetMarketResponse> {
    @Override
    protected GetMarketResponse getResponse(APIGatewayProxyRequestEvent input, Context context) {
        return GetMarketResponse.builder().data("hello world!").build();
    }
}
