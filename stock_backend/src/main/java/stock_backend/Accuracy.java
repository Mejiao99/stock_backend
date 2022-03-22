package stock_backend;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Accuracy {
    private double accuracy;
    private String foo;
    private List<String> accounts;
}
