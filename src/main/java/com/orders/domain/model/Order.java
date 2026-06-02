package com.orders.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String id;
    private Customer customer;
    private List<OrderItem> items;
    private Double totalAmount;
    private LocalDateTime createdAt;

    public void validate() {
        if (customer == null) {
            throw new IllegalArgumentException("Customer details are required");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one product");
        }
        for (OrderItem item : items) {
            if (item.getProductId() == null || item.getProductId().isBlank()) {
                throw new IllegalArgumentException("Product ID cannot be blank");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero");
            }
            if (item.getUnitPrice() == null || item.getUnitPrice() <= 0) {
                throw new IllegalArgumentException("Unit price must be greater than zero");
            }
        }
    }

    public void calculateTotal(java.util.function.Function<Double,Double> discountFunction) {
        if (items == null) {
            this.totalAmount = 0.0;
        } else {
            double baseTotal = items.stream()
                    .mapToDouble(OrderItem::getSubTotal)
                    .sum();
            this.totalAmount = discountFunction.apply(baseTotal);
        }
    }

    public void calculateTotal(){
        calculateTotal(java.util.function.Function.identity());
    }
}
