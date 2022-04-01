package stock.backend;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GetPortfoliosHandler extends AbstractRequestHandler<GetPortfolioResponse> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected GetPortfolioResponse getResponse(APIGatewayProxyRequestEvent input, Context context) {
        final String contents = readContents();
        return convertFromJson(contents);
    }

    private String readContents() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("PortfolioDefinitions");

        Item row = table.getItem(new KeyAttribute("userId","x@x.com"));

        String contents = row.getString("contents");
        return contents;
    }

    private GetPortfolioResponse convertFromJson(String json) {
        try {
            return objectMapper.readValue(json, GetPortfolioResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
