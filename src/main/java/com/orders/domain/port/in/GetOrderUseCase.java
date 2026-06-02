package com.orders.domain.port.in;

import com.orders.domain.model.Order;
import java.util.Optional;

public interface GetOrderUseCase {
    Optional<Order> getOrderById(String id);
}
