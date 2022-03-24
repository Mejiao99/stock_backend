package stockhelper.main;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@AllArgsConstructor
@Getter
@ToString
public class Transaction {
    private String ticket;
    private Integer quantity;
    private String account;
    private TransactionOperation operation;
    private Instant date;
}
