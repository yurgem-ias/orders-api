package com.orders.domain.usecase;

import java.util.List;

import com.orders.domain.exception.OrderNotFoundException;
import com.orders.domain.model.Order;
import com.orders.domain.port.in.GetOrdersByCustomerReactiveUseCase;
import com.orders.domain.port.out.OrderRepositoryPort;
import reactor.core.publisher.Flux;

public class GetOrdersByCustomerReactiveUseCaseImpl implements GetOrdersByCustomerReactiveUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private static final String DOCUMENT_REGEX = "^\\d{5,12}$";

    public GetOrdersByCustomerReactiveUseCaseImpl(OrderRepositoryPort orderRepositoryPort){
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public Flux<Order> getOrdersByCustomerDocumentReactive(String documentNumber){
        if (documentNumber == null || documentNumber.isBlank()) {
            return Flux.error(new IllegalArgumentException("Customer document number cannot be null or empty"));
        }
        if (!documentNumber.matches(DOCUMENT_REGEX)) {
            return Flux.error(new IllegalArgumentException("Customer document number must be numeric and between 5 and 12 digits"));
        }

        return Flux.defer(()->{
            try{
                List<Order> orders = orderRepositoryPort.findByCustomerDocumentNumber(documentNumber);
                return Flux.fromIterable(orders);
            } catch (Exception e) {
                return Flux.error(e);
            }
        })
        .filter(order -> order.getId() != null)
        .map(order ->{
            if (order.getCustomer() != null && order.getCustomer().getName() != null) {
                order.getCustomer().setName(order.getCustomer().getName().trim());
            }
            return order;
        })
        .switchIfEmpty(Flux.error(new OrderNotFoundException("No orders found for customer with document: " + documentNumber)));
    }
}
