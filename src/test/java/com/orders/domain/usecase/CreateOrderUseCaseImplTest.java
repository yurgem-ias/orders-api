package com.orders.domain.usecase;

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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseImplTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @InjectMocks
    private CreateOrderUseCaseImpl createOrderUseCase;

    private Order valiOrder;

    @BeforeEach
    public void setUp() {
        Customer customer = Customer.builder().name("Yurgen prado").build();
        OrderItem item = OrderItem.builder().productId("PROD-01").quantity(2).unitPrice(10.0).build();
        valiOrder = Order.builder().customer(customer).items(List.of(item)).build();
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(createOrderUseCase.createOrder(valiOrder))
                .expectNextMatches(created -> {
                    return created.getId() != null &&
                            created.getCreatedAt() != null &&
                            created.getTotalAmount() != null;
                })
                .verifyComplete();

        verify(orderRepositoryPort, times(1)).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenOrderIsNull() {
        StepVerifier.create(createOrderUseCase.createOrder(null))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Order data cannot be null"))
                .verify();

        verify(orderRepositoryPort, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenOrderIsInvalid() {
        valiOrder.setCustomer(null);

        StepVerifier.create(createOrderUseCase.createOrder(valiOrder))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Customer details are required"))
                .verify();

        verify(orderRepositoryPort, never()).save(any());
    }
}