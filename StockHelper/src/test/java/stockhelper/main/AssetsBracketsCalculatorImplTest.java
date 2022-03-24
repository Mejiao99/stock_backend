package stockhelper.main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AssetsBracketsCalculatorImplTest {
    private AssetsBracketsCalculatorImpl calculator;


    @BeforeEach
    public void setup() {
        calculator = new AssetsBracketsCalculatorImpl();
    }

    @Test
    public void when_execute_current_transaction_assets_convert_to_map_date_portfolio() {
        // Preparation
        List<Transaction> transactions = Arrays.asList(
                new Transaction("a", 12, "x", TransactionOperation.BUY, Instant.parse("2007-05-02T18:00:00.00Z")),
                new Transaction("a", 16, "x", TransactionOperation.BUY, Instant.parse("2007-05-02T19:00:00.00Z")),
                new Transaction("a", 23, "x", TransactionOperation.BUY, Instant.parse("2007-05-04T18:00:00.00Z")),
                new Transaction("a", 16, "x", TransactionOperation.BUY, Instant.parse("2007-07-27T18:00:00.00Z")),
                new Transaction("a", 40, "x", TransactionOperation.BUY, Instant.parse("2008-01-02T18:00:00.00Z"))

        );


        // Execute
        Map<LocalDate, List<InvestmentLine>> assets = calculator.calculate(transactions);


        // Validations
        assertNotNull(assets);
        assertEquals(4, assets.size());

        assertEquals(
                Collections.singletonList(new InvestmentLine("a", 28, "x")),
                assets.get(LocalDate.of(2007, 5, 2)));
        assertEquals(
                Collections.singletonList(new InvestmentLine("a", 51, "x")),
                assets.get(LocalDate.of(2007, 5, 4)));
        assertEquals(
                Collections.singletonList(new InvestmentLine("a", 67, "x")),
                assets.get(LocalDate.of(2007, 7, 27)));
        assertEquals(
                Collections.singletonList(new InvestmentLine("a", 107, "x")),
                assets.get(LocalDate.of(2008, 1, 2)));

    }

}
