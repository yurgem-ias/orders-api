package com.orders.infrastructure.adapter.in.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.orders.domain.event.OrderCreatedEvent;
import com.orders.infrastructure.config.RabbitMQConfig;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receiveNotification(OrderCreatedEvent event){
        log.info("NotificationConsumer: Received OrderCreatedEvent: {}", event);

        if (event.getCustomerEmail() == null || event.getCustomerEmail().isBlank()) {
            log.warn("Invalid customer email in event. Rejecting message to DLQ.");
            throw new IllegalArgumentException("Customer email cannot be empty");
        }
        log.info("Notification sent successfully to {}", event.getCustomerEmail());
    }
}
