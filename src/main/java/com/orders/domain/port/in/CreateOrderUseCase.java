package com.orders.domain.port.in;

import com.orders.domain.model.Order;

public interface CreateOrderUseCase {
    Order createOrder(Order order);
}
