package stockhelper.main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PerAccountTransactionDecomposerTest {
    private PerAccountTransactionDecomposer decomposer;

    @BeforeEach
    public void setup() {
        decomposer = new PerAccountTransactionDecomposer(new SingleAccountTransactionDecomposer());
    }

    @Test
    public void buy_sell_few_stocks() {
        // Preparation
        List<InvestmentLine> fromAllocations = Arrays.asList(
                new InvestmentLine("A", 512, "x"),
                new InvestmentLine("B", 100, "x"),
                new InvestmentLine("A", 777, "y"),
                new InvestmentLine("A", 160, "w")
        );
        List<InvestmentLine> toAllocations = Arrays.asList(
                new InvestmentLine("A", 999, "x"),
                new InvestmentLine("A", 210, "z"),
                new InvestmentLine("A", 160, "w")
        );

        // Execution
        List<Transaction> transactions = decomposer.decompose(fromAllocations, toAllocations);

        // Validations
        assertNotNull(transactions);
        assertEquals(4, transactions.size());

        validateTransaction(find(transactions, "A", "x"), "A", 487, "x", TransactionOperation.BUY);
        validateTransaction(find(transactions, "B", "x"), "B", 100, "x", TransactionOperation.SELL);
        validateTransaction(find(transactions, "A", "y"), "A", 777, "y", TransactionOperation.SELL);
        validateTransaction(find(transactions, "A", "z"), "A", 210, "z", TransactionOperation.BUY);
        assertNull(find(transactions, "A", "w"));
    }

    private void validateTransaction(Transaction transaction, String ticket, int quantity, String account, Enum operation) {
        assertEquals(ticket, transaction.getTicket());
        assertEquals(quantity, transaction.getQuantity());
        assertEquals(account, transaction.getAccount());
        assertEquals(operation, transaction.getOperation());
    }

    private Transaction find(List<Transaction> transactions, String ticket, String account) {
        for (Transaction transaction : transactions) {
            if (transaction.getTicket().equals(ticket) && transaction.getAccount().equals(account)) {
                return transaction;
            }
        }
        return null;
    }

}
