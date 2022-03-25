package stock.backend;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Holding {
    private int quantity;
    private String ticket;
}
