package stock.backend;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Account {
    private String id;
    private Map<String,Double> holdings;
}
