package com.orders.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orders.domain.model.Customer;
import com.orders.domain.model.Order;
import com.orders.domain.model.OrderItem;
import com.orders.domain.port.out.OrderRepositoryPort;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseImplTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @InjectMocks
    private CreateOrderUseCaseImpl createOrderUseCase;

    private Order valiOrder;

    @BeforeEach
    void setUp() {
        Customer customer = Customer.builder().name("Yurgen prado").build();
        OrderItem item = OrderItem.builder().productId("PROD-01").quantity(2).unitPrice(10.0).build();
        valiOrder = Order.builder().customer(customer).items(List.of(item)).build();
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order creatOrder = createOrderUseCase.createOrder(valiOrder);

        assertNotNull(creatOrder);
        assertNotNull(creatOrder.getId());
        assertNotNull(creatOrder.getCreatedAt());
        assertNotNull(creatOrder.getTotalAmount());

        verify(orderRepositoryPort, times(1)).save(valiOrder);
    }

    @Test
    void shouldThrowExceptionWhenOrderIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            createOrderUseCase.createOrder(null);
        });
        assertEquals("Order data cannot be null", exception.getMessage());
        verify(orderRepositoryPort, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenOrderIsInvalid() {
        valiOrder.setCustomer(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            createOrderUseCase.createOrder(valiOrder);
        });
        assertEquals("Customer details are required", exception.getMessage());
        verify(orderRepositoryPort, never()).save(any());
    }

}
