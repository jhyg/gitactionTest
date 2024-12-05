package kyusootest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import kyusootest.domain.*;
import org.junit.Before;
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
@AutoConfigureMessageVerifier
public class PlaceorderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        PlaceorderTest.class
    );

    @Autowired
    private MessageCollector messageCollector;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MessageVerifier<Message<?>> messageVerifier;

    @Autowired
    public OrderRepository repository;

    @Test
    @SuppressWarnings("unchecked")
    public void test0() {
        //given:
        Order entity = new Order();

        entity.setId("N/A");
        entity.setProductName("N/A");
        entity.setProductId("N/A");
        entity.setQty("N/A");
        entity.setTest(true);

        repository.save(entity);

        //when:
        try {
            Order entity = new Order();

            entity.setId("N/A");
            entity.setProductName("N/A");
            entity.setProductId("N/A");
            entity.setQty("N/A");
            entity.setTest("N/A");

            repository.save(entity);

            //then:
            this.messageVerifier.send(
                    MessageBuilder
                        .withPayload(entity)
                        .setHeader(
                            MessageHeaders.CONTENT_TYPE,
                            MimeTypeUtils.APPLICATION_JSON
                        )
                        .build(),
                    "kyusootest"
                );

            Message<?> receivedMessage =
                this.messageVerifier.receive(
                        "kyusootest",
                        5000,
                        TimeUnit.MILLISECONDS
                    );
            assertNotNull("Resulted event must be published", receivedMessage);

            String receivedPayload = (String) receivedMessage.getPayload();

            OrderPlaced outputEvent = objectMapper.readValue(
                receivedPayload,
                OrderPlaced.class
            );

            LOGGER.info("Response received: {}", outputEvent);

            assertEquals(outputEvent.getId(), "N/A");
            assertEquals(outputEvent.getProductName(), "N/A");
            assertEquals(outputEvent.getProductId(), "N/A");
            assertEquals(outputEvent.getQty(), "N/A");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }
}
