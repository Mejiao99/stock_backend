package stock.backend;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Account {
    private String id;
    private List<Holding> holdings;
}
