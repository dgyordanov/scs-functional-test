package edu.spring.cloud.stream.functionaltest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class FunctionalTestApplicationTests {

	@Autowired
	private OrderService orderService;

	@Autowired
	private OutputDestination outputDestination;

	@Test
	void orderChangedTest() {
		orderService.changeOrder();
		Message<byte[]> event = outputDestination.receive(100, "edu.events.orderEvents");
		assertNotNull(event);
	}

}
