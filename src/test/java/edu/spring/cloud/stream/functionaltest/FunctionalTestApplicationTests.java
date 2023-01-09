package edu.spring.cloud.stream.functionaltest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

        final List<Message<?>> messages = new ArrayList<>(1);
        channels.stream()
                .filter(ch -> "edu.events.orderEvents.destination".equals(getChannelName(ch)))
                .forEach(ch -> ch.subscribe(m -> {
                            System.out.println("%%%%% Second subscriber: " + m);
                            messages.add(m);
                        }
                ));
        // #### END  workaround to consume events from channels if a subscriber in the prod code exists.

        orderService.changeOrder();

        assertThat(messages.size()).isEqualTo(1);
        assertNotNull(messages.get(0).getPayload());
    }

    private static String getChannelName(SubscribableChannel ch) {
        Field chNamField = ReflectionUtils.findField(PublishSubscribeChannel.class, "fullChannelName");
        chNamField.setAccessible(true);
        try {
            return (String) chNamField.get(ch);
        } catch (IllegalAccessException e) {
            System.err.println(e);
            return "";
        }
    }

}
