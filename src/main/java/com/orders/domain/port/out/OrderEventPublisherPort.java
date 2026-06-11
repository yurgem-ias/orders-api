package com.orders.domain.port.out;

import com.orders.domain.event.OrderCreatedEvent;
import reactor.core.publisher.Mono;

public interface OrderEventPublisherPort {
    Mono<Void> publishOrderCreated(OrderCreatedEvent event);
}
