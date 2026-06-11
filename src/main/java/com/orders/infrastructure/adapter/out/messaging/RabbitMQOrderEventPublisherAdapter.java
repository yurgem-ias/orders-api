package com.orders.infrastructure.adapter.out.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.orders.domain.event.OrderCreatedEvent;
import com.orders.domain.port.out.OrderEventPublisherPort;
import com.orders.infrastructure.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQOrderEventPublisherAdapter implements OrderEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public Mono<Void> publishOrderCreated(OrderCreatedEvent event) {
        return Mono.fromRunnable(()->{
            log.info("Publishing OrderCreatedEvent to RabbitMQ: {}", event);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, event);
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }
}
