package com.orders.domain.event;

import java.time.LocalDateTime;

public class OrderCreatedEvent {
    private final String orderId;
    private final String customerEmail;
    private final Double totalAmount;
    private final LocalDateTime createdAt;

    public OrderCreatedEvent(String orderId, String customerEmail, Double totalAmount, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.customerEmail = customerEmail;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "OrderCreatedEvent{" +
                "orderId='" + orderId + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", totalAmount=" + totalAmount +
                ", createdAt=" + createdAt +
                '}';
    }
}
