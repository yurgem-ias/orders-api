package com.orders.infrastructure.adapter.in.web;

import com.orders.domain.model.Customer;
import com.orders.domain.model.Order;
import com.orders.domain.model.OrderItem;
import com.orders.infrastructure.adapter.in.web.dto.CustomerDto;
import com.orders.infrastructure.adapter.in.web.dto.CreateOrderRequest;
import com.orders.infrastructure.adapter.in.web.dto.OrderItemDto;
import com.orders.infrastructure.adapter.in.web.dto.OrderResponse;

import java.util.stream.Collectors;

public class OrderWebMapper {

    public static Order toDomain(CreateOrderRequest request) {
        if (request == null) {
            return null;
        }
        
        Customer customer = null;
        if (request.getCustomer() != null) {
            customer = Customer.builder()
                    .name(request.getCustomer().getName())
                    .email(request.getCustomer().getEmail())
                    .documentType(request.getCustomer().getDocumentType())
                    .documentNumber(request.getCustomer().getDocumentNumber())
                    .build();
        }
        
        return Order.builder()
                .customer(customer)
                .items(request.getItems() == null ? null : request.getItems().stream()
                        .map(itemDto -> OrderItem.builder()
                                .productId(itemDto.getProductId())
                                .quantity(itemDto.getQuantity())
                                .unitPrice(itemDto.getUnitPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public static OrderResponse toResponse(Order order) {
        if (order == null) {
            return null;
        }
        
        CustomerDto customerDto = null;
        if (order.getCustomer() != null) {
            customerDto = CustomerDto.builder()
                    .name(order.getCustomer().getName())
                    .email(order.getCustomer().getEmail())
                    .documentType(order.getCustomer().getDocumentType())
                    .documentNumber(order.getCustomer().getDocumentNumber())
                    .build();
        }
        
        return OrderResponse.builder()
                .id(order.getId())
                .customer(customerDto)
                .items(order.getItems() == null ? null : order.getItems().stream()
                        .map(item -> OrderItemDto.builder()
                                .productId(item.getProductId())
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnitPrice())
                                .build())
                        .collect(Collectors.toList()))
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .build();
    }
}