package kyusootest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kyusootest.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifier;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.cloud.contract.verifier.messaging.internal.ContractVerifierMessage;
import org.springframework.cloud.contract.verifier.messaging.internal.ContractVerifierMessaging;
import org.springframework.cloud.contract.verifier.messaging.internal.ContractVerifierObjectMapper;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MimeTypeUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DecreaseStockTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        DecreaseStockTest.class
    );

    @Autowired
    private MessageCollector messageCollector;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MessageVerifier<Message<?>> messageVerifier;

    @Autowired
    public InventoryRepository repository;

    @Test
    @SuppressWarnings("unchecked")
    public void test0() {
        //given:
        Inventory entity = new Inventory();

        entity.setId("1");
        entity.setStock(100);
        entity.setProductName("TestProduct");
        entity.setProductCode(ProductCode.P1);
        entity.setMoney(new Money(10.0, "USD"));

        repository.save(entity);

        //when:

        OrderPlaced event = new OrderPlaced();

        event.setId(entity.getId());
        event.setProductName(entity.getProductName());
        event.setProductId("P1");
        event.setQty(10);

        InventoryApplication.applicationContext = applicationContext;

        ObjectMapper objectMapper = new ObjectMapper()
            .configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false
            );
        try {
            this.messageVerifier.send(
                    MessageBuilder
                        .withPayload(objectMapper.writeValueAsString(event))
                        .setHeader(
                            MessageHeaders.CONTENT_TYPE,
                            MimeTypeUtils.APPLICATION_JSON
                        )
                        .setHeader("type", event.getEventType())
                        .build(),
                    "kyusootest"
                );

            //then:

            Inventory result = repository.findById(entity.getId()).get();

            LOGGER.info("Response received: {}", result);

            assertEquals(result.getId(), entity.getId());
            assertEquals(result.getStock().intValue(), 90); // 100 - 10
            assertEquals(result.getProductName(), entity.getProductName());
            assertEquals(result.getProductCode(), ProductCode.P2);
            assertEquals(result.getMoney(), entity.getMoney());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }
}
