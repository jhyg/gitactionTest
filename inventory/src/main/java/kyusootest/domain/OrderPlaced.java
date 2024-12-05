package kyusootest.domain;

import java.util.*;
import kyusootest.domain.*;
import kyusootest.infra.AbstractEvent;
import lombok.*;

@Data
@ToString
public class OrderPlaced extends AbstractEvent {

    private Long id;
    private String productName;
    private String productId;
    private Integer qty;
}
