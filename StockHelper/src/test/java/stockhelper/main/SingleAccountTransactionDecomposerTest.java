package stockhelper.main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SingleAccountTransactionDecomposerTest {
    //TODO: Test if null inputs
    //TODO: Validations inputs
    //TODO: What If tickets repeats in same list, same account
    //TODO: MultipleAccount

    private SingleAccountTransactionDecomposer decomposer;

    @BeforeEach
    public void setup() {
        decomposer = new SingleAccountTransactionDecomposer();
    }

    @Test
    public void only_buy() {
        // Preparation
        List<InvestmentLine> fromAllocations = Arrays.asList();
        List<InvestmentLine> toAllocations = Arrays.asList(new InvestmentLine("A", 750, "c"));


        // Execution
        List<Transaction> transactionsList = decomposer.decompose(fromAllocations, toAllocations);

        // Validations
        assertNotNull(transactionsList);
        assertEquals(1, transactionsList.size());
        validateTransaction(find(transactionsList, "A"), "A", 750, "c", TransactionOperation.BUY);
    }

    @Test
    public void only_sell() {
        // Preparation
        List<InvestmentLine> fromAllocations = Arrays.asList(new InvestmentLine("A", 999, "c"));
        List<InvestmentLine> toAllocations = Arrays.asList();

        // Execution
        List<Transaction> transactionsList = decomposer.decompose(fromAllocations, toAllocations);

        // Validations
        assertNotNull(transactionsList);
        assertEquals(1, transactionsList.size());
        validateTransaction(find(transactionsList, "A"), "A", 999, "c", TransactionOperation.SELL);
    }

    @Test
    public void no_sell_no_buy() {
        // Preparation
        List<InvestmentLine> fromAllocations = Arrays.asList(new InvestmentLine("A", 999, "c"));
        List<InvestmentLine> toAllocations = Arrays.asList(new InvestmentLine("A", 999, "c"));

        // Execution
        List<Transaction> transactionsList = decomposer.decompose(fromAllocations, toAllocations);

        // Validations
        assertNotNull(transactionsList);
        assertEquals(0, transactionsList.size());
    }

    @Test
    public void buy_few_stocks() {
        // Preparation
        List<InvestmentLine> fromAllocations = Arrays.asList(new InvestmentLine("A", 750, "c"), new InvestmentLine("B", 10, "c"), new InvestmentLine("C", 100, "c"));
        List<InvestmentLine> toAllocations = Arrays.asList(new InvestmentLine("A", 999, "c"), new InvestmentLine("B", 230, "c"), new InvestmentLine("C", 500, "c"));

        // Execution
        List<Transaction> transactionsList = decomposer.decompose(fromAllocations, toAllocations);

        // Validations
        assertNotNull(transactionsList);
        assertEquals(3, transactionsList.size());

        validateTransaction(find(transactionsList, "A"), "A", 249, "c", TransactionOperation.BUY);
        validateTransaction(find(transactionsList, "B"), "B", 220, "c", TransactionOperation.BUY);
        validateTransaction(find(transactionsList, "C"), "C", 400, "c", TransactionOperation.BUY);
    }

    @Test
    public void sell_few_stocks() {
        // Preparation
        List<InvestmentLine> fromAllocations = Arrays.asList(new InvestmentLine("A", 999, "c"), new InvestmentLine("B", 230, "c"), new InvestmentLine("C", 500, "c"));
        List<InvestmentLine> toAllocations = Arrays.asList(new InvestmentLine("A", 750, "c"), new InvestmentLine("B", 10, "c"), new InvestmentLine("C", 100, "c"));

        // Execution
        List<Transaction> transactionsList = decomposer.decompose(fromAllocations, toAllocations);

        // Validations
        assertNotNull(transactionsList);
        assertEquals(3, transactionsList.size());

        validateTransaction(find(transactionsList, "A"), "A", 249, "c", TransactionOperation.SELL);
        validateTransaction(find(transactionsList, "B"), "B", 220, "c", TransactionOperation.SELL);
        validateTransaction(find(transactionsList, "C"), "C", 400, "c", TransactionOperation.SELL);
    }

    @Test
    public void buy_and_sell_few_stocks() {
        // Preparation
        List<InvestmentLine> fromAllocations = Arrays.asList(new InvestmentLine("A", 999, "c"), new InvestmentLine("B", 10, "c"), new InvestmentLine("C", 100, "c"), new InvestmentLine("D", 54, "c"));
        List<InvestmentLine> toAllocations = Arrays.asList(new InvestmentLine("A", 750, "c"), new InvestmentLine("B", 230, "c"), new InvestmentLine("C", 500, "c"), new InvestmentLine("D", 54, "c"));

        // Execution
        List<Transaction> transactionsList = decomposer.decompose(fromAllocations, toAllocations);

        // Validations
        assertNotNull(transactionsList);
        assertEquals(3, transactionsList.size());

        validateTransaction(find(transactionsList, "A"), "A", 249, "c", TransactionOperation.SELL);
        validateTransaction(find(transactionsList, "B"), "B", 220, "c", TransactionOperation.BUY);
        validateTransaction(find(transactionsList, "C"), "C", 400, "c", TransactionOperation.BUY);
    }

    @Test
    public void empty_from_and_to_investments_list() {
        // Preparation
        List<InvestmentLine> fromAllocations = Collections.emptyList();
        List<InvestmentLine> toAllocations = Collections.emptyList();

        // Execution
        List<Transaction> transactionsList = decomposer.decompose(fromAllocations, toAllocations);

        // Validations
        assertNotNull(transactionsList);
        assertEquals(0, transactionsList.size());
    }

    private void validateTransaction(Transaction transaction, String ticket, int quantity, String account, Enum operation) {
        assertEquals(ticket, transaction.getTicket());
        assertEquals(quantity, transaction.getQuantity());
        assertEquals(account, transaction.getAccount());
        assertEquals(operation, transaction.getOperation());
    }

    private Transaction find(List<Transaction> transactionList, String ticket) {
        for (Transaction transaction : transactionList) {
            if (transaction.getTicket().equals(ticket)) {
                return transaction;
            }
        }
        return null;
    }

}
