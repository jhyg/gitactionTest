package kyusootest.domain;

import java.time.LocalDate;
import java.util.*;
import kyusootest.domain.*;
import kyusootest.infra.AbstractEvent;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class StockDecreased extends AbstractEvent {

    private Long id;
    private Integer stock;
    private String productName;
    private ProductCode productCode;

    public StockDecreased(Inventory aggregate) {
        super(aggregate);
    }

    public StockDecreased() {
        super();
    }
}
//>>> DDD / Domain Event
