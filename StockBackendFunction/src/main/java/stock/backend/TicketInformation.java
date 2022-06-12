package stock.backend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketInformation {
    private String symbol;
    private String currency;
    private double regularMarketPreviousClose;
}
