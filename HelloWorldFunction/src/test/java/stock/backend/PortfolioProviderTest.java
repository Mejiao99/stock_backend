package stock.backend;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import helloworld.App;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PortfolioProviderTest {

    @Test
    public void successfulResponse() {
        App app = new App();
        APIGatewayProxyResponseEvent result = app.handleRequest(null, null);
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"id\""));
        assertTrue(content.contains("\"name\""));
        assertTrue(content.contains("\"accuracy\""));
        assertTrue(content.contains("\"totalHoldings\""));
        assertTrue(content.contains("\"amount\""));
        assertTrue(content.contains("\"currency\""));
    }
}