package stock.backend;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PortfolioDefinition {
    private String id;
    private String name;
    private List<Account> accounts;
}
