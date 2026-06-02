package com.orders.infrastructure.adapter.out.persistence;

import com.orders.domain.model.Order;
import com.orders.domain.port.out.OrderRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOrderRepositoryAdapter implements OrderRepositoryPort {

    private final Map<String, Order> database = new ConcurrentHashMap<>();

    @Override
    public Order save(Order order) {
        if (order == null || order.getId() == null) {
            throw new IllegalArgumentException("Order and Order ID cannot be null");
        }
        database.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public List<Order> findByCustomerDocumentNumber(String documentNumber){
        if(documentNumber == null){
            return List.of();
        }
        return database.values().stream()
                .filter(order -> order.getCustomer() != null && documentNumber.equals(order.getCustomer().getDocumentNumber()))
                .toList();
    }
}
