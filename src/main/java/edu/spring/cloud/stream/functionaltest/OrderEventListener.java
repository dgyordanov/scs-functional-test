package edu.spring.cloud.stream.functionaltest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class OrderEventListener {

    @Bean
    public Consumer<String> orderEvents() {
        // React on order events
        return e -> System.out.println("### Order Event: " + e);
    }

}
