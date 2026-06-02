package com.orders.domain.port.in;

import java.util.List;

import com.orders.domain.model.Order;

public interface GetOrdersByCustomerUseCase {
    List<Order> getOrdersByCustomerDocument(String documentNumber);
}
