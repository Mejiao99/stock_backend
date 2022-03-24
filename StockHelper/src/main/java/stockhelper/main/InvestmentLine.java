package stockhelper.main;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class InvestmentLine {
    private String ticket;
    private Integer quantity;
    private String account;
}
