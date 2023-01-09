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
        SubscribableChannel channel = ScsChannelsTestUtil.getChannelByTopicName("edu.events.orderEvents", inputDestination);

        final List<Message<?>> messages = new ArrayList<>(1);
        channel.subscribe(m -> {
                    System.out.println("%%%%% Second subscriber: " + m);
                    messages.add(m);
                }
        );

        orderService.changeOrder();

        assertThat(messages.size()).isEqualTo(1);
        assertNotNull(messages.get(0).getPayload());
    }

}
