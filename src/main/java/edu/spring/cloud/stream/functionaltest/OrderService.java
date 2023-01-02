package edu.spring.cloud.stream.functionaltest;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final StreamBridge streamBridge;

    public OrderService(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void changeOrder() {
        // Some order changes
        streamBridge.send("orderEvents-out-0", "Test Order Change Event");
    }

}
