package kyusootest.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import kyusootest.OrderApplication;
import kyusootest.domain.OrderPlaced;
import lombok.Data;

@Entity
@Table(name = "Order_table")
@Data
//<<< DDD / Aggregate Root
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String productName;

    private String productId;

    private Integer qty;

    private Boolean test;

    @PostPersist
    public void onPostPersist() {
        kyusootest.external.GetStockQuery getStockQuery = new kyusootest.external.GetStockQuery();
        // getStockQuery.set??()
        Inventory inventory = OrderApplication.applicationContext
            .getBean(kyusootest.external.InventoryService.class)
            .getStock(getStockQuery);

        OrderPlaced orderPlaced = new OrderPlaced(this);
        orderPlaced.publishAfterCommit();
    }

    public static OrderRepository repository() {
        OrderRepository orderRepository = OrderApplication.applicationContext.getBean(
            OrderRepository.class
        );
        return orderRepository;
    }
}
//>>> DDD / Aggregate Root
