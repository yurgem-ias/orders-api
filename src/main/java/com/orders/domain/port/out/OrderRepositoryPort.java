package com.orders.domain.port.out;

import com.orders.domain.model.Order;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepositoryPort {

    Mono<Order> save(Order order);
    Mono<Order> findById(String id);
    Flux<Order> findByCustomerDocumentNumber(String documentNumber);
}
