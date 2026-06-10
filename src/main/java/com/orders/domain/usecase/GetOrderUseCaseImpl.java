package com.orders.domain.usecase;

import com.orders.domain.model.Order;
import com.orders.domain.port.in.GetOrderUseCase;
import com.orders.domain.port.out.OrderRepositoryPort;
import reactor.core.publisher.Mono;

public class GetOrderUseCaseImpl implements GetOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    public GetOrderUseCaseImpl(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public Mono<Order> getOrderById(String id) {
        if (id == null || id.isBlank()) {
            return Mono.error(new IllegalArgumentException("Order ID cannot be null or empty"));
        }
        return orderRepositoryPort.findById(id);
    }
}
