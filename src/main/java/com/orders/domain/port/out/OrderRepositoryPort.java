package com.orders.domain.port.out;

import com.orders.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(String id);
    List<Order> findByCustomerDocumentNumber(String documentNumber);
}
