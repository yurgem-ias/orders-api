package com.orders.domain.usecase;

import com.orders.domain.exception.OrderNotFoundException;
import com.orders.domain.model.Order;
import com.orders.domain.port.in.GetOrdersByCustomerUseCase;
import com.orders.domain.port.out.OrderRepositoryPort;
import reactor.core.publisher.Flux;

public class GetOrdersByCustomerUseCaseImpl implements GetOrdersByCustomerUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private static final String DOCUMENT_REGEX = "^\\d{5,12}$";

    public GetOrdersByCustomerUseCaseImpl(OrderRepositoryPort orderRepositoryPort){
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public Flux<Order> getOrdersByCustomerDocument(String documentNumber) {
        if (documentNumber == null || documentNumber.isBlank()){
            return Flux.error(new IllegalArgumentException("Customer document number cannot be null or empty"));
        }
        if (!documentNumber.matches(DOCUMENT_REGEX)) {
            return Flux.error(new IllegalArgumentException("Customer document number must be numeric and between 5 and 12 digits"));
        }
        return orderRepositoryPort.findByCustomerDocumentNumber(documentNumber)
                .filter(order -> order.getId() != null)
                .map(order -> {
                    if (order.getCustomer() != null && order.getCustomer().getName() != null) {
                        order.getCustomer().setName(order.getCustomer().getName().trim());
                    }
                    return order;
                })
                .switchIfEmpty(Flux.error(new OrderNotFoundException("No orders found for customer with document: " + documentNumber)));
    }
}
