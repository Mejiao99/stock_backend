package stock.backend;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

public class GetTableHandler extends AbstractRequestHandler<GetTableResponse> {
    GetTableResponse contents = new GetTableResponse();
    @Override
    protected GetTableResponse getResponse(APIGatewayProxyRequestEvent input, Context context) {
        return contents;
    }

}
