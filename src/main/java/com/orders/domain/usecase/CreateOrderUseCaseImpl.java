package com.orders.domain.usecase;

import com.orders.domain.model.Order;
import com.orders.domain.port.in.CreateOrderUseCase;
import com.orders.domain.port.out.OrderRepositoryPort;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateOrderUseCaseImpl implements CreateOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    public CreateOrderUseCaseImpl(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public Mono<Order> createOrder(Order order) {
        if (order == null) {
            return Mono.error(new IllegalArgumentException("Order data cannot be null"));
        }
    
        try {
        order.validate();
        
        order.setId(UUID.randomUUID().toString());
        order.setCreatedAt(LocalDateTime.now());
        
        order.calculateTotal();
        
        return orderRepositoryPort.save(order);
        } catch (Exception e) {
            return Mono.error(e);
        }

}
}
