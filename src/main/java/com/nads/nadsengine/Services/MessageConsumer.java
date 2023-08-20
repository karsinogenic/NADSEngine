package com.nads.nadsengine.Services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    // @RabbitListener(queues = "<queue-name>")
    public void receiveMessage(String message) {
        // Process the received message
        System.out.println("Received message: " + message);
    }
}
