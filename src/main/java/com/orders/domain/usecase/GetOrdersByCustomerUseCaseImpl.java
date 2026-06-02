package com.orders.domain.usecase;

import java.util.List;

import com.orders.domain.model.Order;
import com.orders.domain.port.in.GetOrdersByCustomerUseCase;
import com.orders.domain.port.out.OrderRepositoryPort;

public class GetOrdersByCustomerUseCaseImpl implements GetOrdersByCustomerUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private static final String DOCUMENT_REGEX= "^\\d{5,12}$";

    public GetOrdersByCustomerUseCaseImpl(OrderRepositoryPort orderRepositoryPort){
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public List<Order> getOrdersByCustomerDocument(String documentNumber) {
        if (documentNumber == null || documentNumber.isBlank()){
            throw new   IllegalArgumentException("Customer document number cannot be null or empty");
        }
        if (!documentNumber.matches(DOCUMENT_REGEX)) {
            throw new IllegalArgumentException("Customer document number must ve numeric and between 5 and 12 digits");
        }
        return orderRepositoryPort.findByCustomerDocumentNumber(documentNumber);
    }
}
