package edu.spring.cloud.stream.functionaltest;

import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

public class ScsChannelsTestUtil {
    public static SubscribableChannel getChannelByTopicName(String topicName, InputDestination inputDestination) throws IllegalAccessException {
        var destinationName = String.format("%s.destination", topicName);

        Field chField = ReflectionUtils.findField(InputDestination.class, "channels");
        chField.setAccessible(true);
        List<SubscribableChannel> channels = (List<SubscribableChannel>) chField.get(inputDestination);

        return channels.stream()
                .filter(ch -> destinationName.equals(getChannelName(ch)))
                .findFirst()
                .orElseThrow();
    }

    static String getChannelName(SubscribableChannel ch) {
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