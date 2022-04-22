package stock.backend;

import java.util.List;

public class GetTableResponse {
    private List<String> accounts;
    private List<String> tickets;
    private List<List<Money>> data;
    private Totals totals;
}
