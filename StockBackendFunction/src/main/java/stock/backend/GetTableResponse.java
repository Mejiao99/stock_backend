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
public class GetTableResponse {
    private List<String> accounts;
    private List<String> tickets;
    private List<List<Money>> data;
    private Totals totals;
}
