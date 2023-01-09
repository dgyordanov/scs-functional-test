package edu.spring.cloud.stream.functionaltest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class FunctionalTestApplicationTests {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OutputDestination outputDestination;

    @Autowired
    private InputDestination inputDestination;

    @Test
    void orderChangedTest() throws InterruptedException, IllegalAccessException {
        // #### workaround to consume events from channels if a subscriber in the prod code exists.
        Field chField = ReflectionUtils.findField(InputDestination.class, "channels");
        chField.setAccessible(true);
        List<SubscribableChannel> channels = (List<SubscribableChannel>) chField.get(inputDestination);
        SubscribableChannel ch = channels.iterator().next(); // or more elaborate code if there are multiple bindings to find your channel
        ch.subscribe((x) -> {
            System.out.println("%%%%% Second subscriber: " + x);
        });
		// #### END  workaround to consume events from channels if a subscriber in the prod code exists.

        orderService.changeOrder();
        Message<byte[]> event = outputDestination.receive(100, "edu.events.orderEvents");

		assertNotNull(event);
    }

}
