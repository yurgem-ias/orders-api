package com.orders.infrastructure.adapter.out.persistence;

import com.orders.domain.model.Order;
import com.orders.domain.port.out.OrderRepositoryPort;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOrderRepositoryAdapter implements OrderRepositoryPort {

    private final Map<String, Order> database = new ConcurrentHashMap<>();

    @Override
    public Mono<Order> save(Order order) {
        if (order == null || order.getId() == null) {
            return Mono.error(new IllegalArgumentException("Order and Order ID cannot be null"));
        }
        database.put(order.getId(), order);
        return Mono.just(order);
    }

    @Override
    public Mono<Order> findById(String id) {
        if (id == null) {
            return Mono.empty();
        }
        Order order = database.get(id);
        return order != null ? Mono.just(order) : Mono.empty();
    }

    @Override
    public Flux<Order> findByCustomerDocumentNumber(String documentNumber) {
        if (documentNumber == null) {
            return Flux.empty();
        }
        return Flux.fromIterable(database.values())
                .filter(order -> order.getCustomer() != null && documentNumber.equals(order.getCustomer().getDocumentNumber()));
    }
}