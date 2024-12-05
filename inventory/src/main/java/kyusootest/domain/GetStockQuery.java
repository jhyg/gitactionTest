package kyusootest.domain;

import java.util.Date;
import lombok.Data;

@Data
public class GetStockQuery {

    private Long id;
    private Integer stock;
    private String productName;
}
