package kyusootest.external;

import java.util.Date;
import lombok.Data;

@Data
public class Inventory {

    private Long id;
    private Integer stock;
    private String productName;
    private Object productCode;
    private Object money;
}
