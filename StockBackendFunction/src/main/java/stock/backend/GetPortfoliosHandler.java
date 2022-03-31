package stock.backend;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GetPortfoliosHandler extends AbstractRequestHandler<GetPortfolioResponse> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected GetPortfolioResponse getResponse(APIGatewayProxyRequestEvent input, Context context) {
        final String contents = "{\n" +
                "  \"portfolios\": [\n" +
                "    {\n" +
                "      \"id\": \"Xl1\",\n" +
                "      \"name\": \"Port1\",\n" +
                "      \"accounts\": [\n" +
                "        {\n" +
                "          \"id\": \"C1\",\n" +
                "          \"holdings\": {\n" +
                "            \"ticketA\": 8,\n" +
                "            \"ticketB\": 5,\n" +
                "            \"ticketC\": 3\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"Xl2\",\n" +
                "      \"name\": \"Port2\",\n" +
                "      \"accounts\": [\n" +
                "        {\n" +
                "          \"id\": \"C1\",\n" +
                "          \"holdings\": {\n" +
                "            \"ticketA\": 8,\n" +
                "            \"ticketB\": 5,\n" +
                "            \"ticketC\": 3\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"C2\",\n" +
                "          \"holdings\": {\n" +
                "            \"ticketD\": 7,\n" +
                "            \"ticketE\": 2\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"C3\",\n" +
                "          \"holdings\": {\n" +
                "            \"ticketA\": 4,\n" +
                "            \"ticketD\": 1\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"stockPrices\": {\n" +
                "    \"ticketA\": {\n" +
                "      \"amount\": 5,\n" +
                "      \"currency\": \"usd\"\n" +
                "    },\n" +
                "    \"ticketB\": {\n" +
                "      \"amount\": 2,\n" +
                "      \"currency\": \"usd\"\n" +
                "    },\n" +
                "    \"ticketC\": {\n" +
                "      \"amount\": 7,\n" +
                "      \"currency\": \"usd\"\n" +
                "    },\n" +
                "    \"ticketD\": {\n" +
                "      \"amount\": 1,\n" +
                "      \"currency\": \"cad\"\n" +
                "    },\n" +
                "    \"ticketE\": {\n" +
                "      \"amount\": 9,\n" +
                "      \"currency\": \"cad\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"conversionRates\": {\n" +
                "    \"usd\": 1.3,\n" +
                "    \"cad\": 1\n" +
                "  },\n" +
                "  \"targetCurrency\": \"cad\"\n" +
                "}";
        return convertFromJson(contents);
    }

    private GetPortfolioResponse convertFromJson(String json) {
        try {
            return objectMapper.readValue(json, GetPortfolioResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
