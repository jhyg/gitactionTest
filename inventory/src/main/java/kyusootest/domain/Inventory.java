package kyusootest.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import kyusootest.InventoryApplication;
import kyusootest.domain.StockDecreased;
import lombok.Data;

@Entity
@Table(name = "Inventory_table")
@Data
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer stock;

    private String productName;

    @Enumerated(EnumType.STRING)
    private ProductCode productCode;

    @Embedded
    private Money money;

    @PostPersist
    public void onPostPersist() {
        StockDecreased stockDecreased = new StockDecreased(this);
        stockDecreased.publishAfterCommit();
    }

    public static InventoryRepository repository() {
        InventoryRepository inventoryRepository = InventoryApplication.applicationContext.getBean(
            InventoryRepository.class
        );
        return inventoryRepository;
    }

    public static void decreaseStock(OrderPlaced orderPlaced) {
        repository()
            .findById(orderPlaced.getId())
            .ifPresent(inventory -> {
                if (
                    orderPlaced.getQty() != null &&
                    inventory.stock >= orderPlaced.getQty()
                ) {
                    inventory.stock -= orderPlaced.getQty();
                    inventory.setProductCode(ProductCode.P2); // ProductCode 변경
                    repository().save(inventory);
                } else {
                    throw new RuntimeException("Insufficient stock");
                }
            });
    }
}
