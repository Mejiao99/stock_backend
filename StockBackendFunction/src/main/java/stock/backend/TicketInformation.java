package stock.backend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketInformation {
    private String symbol;
    private List<Integer> timestamp;
    private List<Double> close;
    private double chartPreviousClose;
}
