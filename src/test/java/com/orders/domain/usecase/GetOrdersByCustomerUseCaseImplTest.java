package com.orders.domain.usecase;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orders.domain.exception.OrderNotFoundException;
import com.orders.domain.model.Customer;
import com.orders.domain.model.Order;
import com.orders.domain.port.out.OrderRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GetOrdersByCustomerUseCaseImplTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @InjectMocks
    private GetOrdersByCustomerUseCaseImpl useCase;

    @Test
    void shouldReturnOrdersForValidDocument(){
        String document = "12345678";
        Order order = Order.builder()
                .id("order-id-1")
                .customer(Customer.builder().documentNumber(document).name("Yurgen Prado  ").build())
                .build();

        when(orderRepositoryPort.findByCustomerDocumentNumber(document)).thenReturn(Flux.just(order));

        StepVerifier.create(useCase.getOrdersByCustomerDocument(document))
                .expectNextMatches(result -> result.getId().equals("order-id-1") && result.getCustomer().getName().equals("Yurgen Prado"))
                .verifyComplete();

        verify(orderRepositoryPort, times(1)).findByCustomerDocumentNumber(document);
    }

    @Test
    void shouldThrowExceptionWhenDocumentIsNull(){
        StepVerifier.create(useCase.getOrdersByCustomerDocument(null))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Customer document number cannot be null or empty"))
                .verify();

        verifyNoInteractions(orderRepositoryPort);
    }

    @Test
    void shouldThrowExceptionWhenDocumentIsBlank(){
        StepVerifier.create(useCase.getOrdersByCustomerDocument("    "))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Customer document number cannot be null or empty"))
                .verify();

        verifyNoInteractions(orderRepositoryPort);
    }

    @Test
    void shouldThrowExceptionWhenDocumentIsInvalidPattern(){
        StepVerifier.create(useCase.getOrdersByCustomerDocument("123"))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("must be numeric and between 5 and 12 digits"))
                .verify();

        StepVerifier.create(useCase.getOrdersByCustomerDocument("1234567890123"))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("must be numeric and between 5 and 12 digits"))
                .verify();

        StepVerifier.create(useCase.getOrdersByCustomerDocument("123asdas321"))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("must be numeric and between 5 and 12 digits"))
                .verify();

        verifyNoInteractions(orderRepositoryPort);
    }

    @Test
    void shouldThrowOrderNotFoundExceptionWhenNoOrdersFound(){
        String document = "12345678";
        when(orderRepositoryPort.findByCustomerDocumentNumber(document)).thenReturn(Flux.empty());

        StepVerifier.create(useCase.getOrdersByCustomerDocument(document))
                .expectError(OrderNotFoundException.class)
                .verify();
    }
}