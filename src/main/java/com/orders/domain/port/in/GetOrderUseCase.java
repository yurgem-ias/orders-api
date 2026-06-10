package com.orders.domain.port.in;

import com.orders.domain.model.Order;

import reactor.core.publisher.Mono;

public interface GetOrderUseCase {
    Mono<Order> getOrderById(String id);
}
