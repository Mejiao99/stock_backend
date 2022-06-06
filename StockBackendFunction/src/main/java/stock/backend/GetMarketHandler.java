package stock.backend;

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

public class GetMarketHandler extends AbstractRequestHandler<GetMarketResponse> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected GetMarketResponse getResponse(APIGatewayProxyRequestEvent input, Context context) {
        String value = "AMZN-2022-05-30";
        final String contents = readContents(value);
        TicketHistoricalInformation response = convertFromJson(contents);
        return GetMarketResponse.builder().data("hello world!").ticket(response).build();
    }

    private String readContents(String value) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("StockMarket");

        Item row = table.getItem(new KeyAttribute("ticketPerDate", value));

        String contents = row.getString("contents");
        return contents;
    }

    private TicketHistoricalInformation convertFromJson(String json) {
        try {
            return objectMapper.readValue(json, TicketHistoricalInformation.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
