package com.orders.domain.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void shouldCreateValidOrder() {
        Customer customer = Customer.builder().name("yurgen prado").build();
        OrderItem item = OrderItem.builder().productId("PROD-01").quantity(2).unitPrice(25.0).build();
        Order order = Order.builder().customer(customer).items(List.of(item)).build();

        assertDoesNotThrow(order::validate);
    }

    @Test
    void shouldThrowExceptionWhenCustomerIsNull() {
        OrderItem item = OrderItem.builder().productId("PROD-01").quantity(2).unitPrice(10.0).build();
        Order order = Order.builder().items(List.of(item)).build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, order::validate);
        assertEquals("Customer details are required", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenItemsAreNullOrEmpty() {
        Customer customer = Customer.builder().name("yurgen prado").build();
        Order order = Order.builder().customer(customer).build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, order::validate);
        assertEquals("Order must contain at least one product", exception.getMessage());

        order.setItems(Collections.emptyList());
        exception = assertThrows(IllegalArgumentException.class, order::validate);
        assertEquals("Order must contain at least one product", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsZeroOrNegative() {
        Customer customer = Customer.builder().name("yurgen prado").build();
        OrderItem item = OrderItem.builder().productId("PROD-01").quantity(0).unitPrice(10.0).build();
        Order order = Order.builder().customer(customer).items(List.of(item)).build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, order::validate);
        assertEquals("Quantity must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUnitPriceIsZeroOrNegative() {
        Customer customer = Customer.builder().name("yurgen prado").build();
        OrderItem item = OrderItem.builder().productId("PROD-01").quantity(2).unitPrice(-5.0).build();
        Order order = Order.builder().customer(customer).items(List.of(item)).build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, order::validate);
        assertEquals("Unit price must be greater than zero",exception.getMessage());
    }

    @Test
    void ShouldCalculateTotalAmountCorrectly() {
        OrderItem item1 = OrderItem.builder().productId("PROD-01").quantity(2).unitPrice(10.0).build();
        OrderItem item2 = OrderItem.builder().productId("PROD-02").quantity(1).unitPrice(15.0).build();

        Order order = Order.builder().items(List.of(item1, item2)).build();
        order.calculateTotal();

        assertEquals(35.0, order.getTotalAmount());
    }

    @Test
    void ShouldCalculateTotalAmountWithDiscount(){
        OrderItem item = OrderItem.builder().productId("PROD-01").quantity(2).unitPrice(15.0).build();
        Order order = Order.builder().items(List.of(item)).build();

        java.util.function.Function<Double,Double> tenPercentDiscount = total -> total *0.90;

        order.calculateTotal(tenPercentDiscount);

        assertEquals(27.0, order.getTotalAmount());
    }
}
