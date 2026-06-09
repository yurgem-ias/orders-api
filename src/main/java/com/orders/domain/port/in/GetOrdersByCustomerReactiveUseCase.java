package com.orders.domain.port.in;

import com.orders.domain.model.Order;
import reactor.core.publisher.Flux;

public interface GetOrdersByCustomerReactiveUseCase {
    Flux<Order> getOrdersByCustomerDocumentReactive(String documentNumber);
}
