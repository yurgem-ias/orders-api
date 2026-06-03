package com.orders.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

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
class GetOrdersByCustomerReactiveUseCaseImplTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @InjectMocks
    private GetOrdersByCustomerReactiveUseCaseImpl useCase;

    @Test
    void shouldReturnOrdersForValidDocumentReactive(){
        String document = "12345678";
        Order order = Order.builder()
                .id("order-id-1")
                .customer(Customer.builder().documentNumber(document).name(" Yurgen Prado").build())
                .build();

        when(orderRepositoryPort.findByCustomerDocumentNumber(document)).thenReturn(List.of(order));

        Flux<Order> resultFlux = useCase.getOrdersByCustomerDocumentReactive(document);

        StepVerifier.create(resultFlux)
                .assertNext(resOrder ->{
                    assertEquals("order-id-1", resOrder.getId());
                    assertEquals("Yurgen Prado", resOrder.getCustomer().getName());
                })
                .verifyComplete();

        verify(orderRepositoryPort, times(1)).findByCustomerDocumentNumber(document);
    }

    @Test
    void shouldEmitErrorWhenNoOrdersFoundReactive(){
        String document = "12345678";
        when(orderRepositoryPort.findByCustomerDocumentNumber(document)).thenReturn(List.of());

        Flux<Order> resultFlux = useCase.getOrdersByCustomerDocumentReactive(document);

        StepVerifier.create(resultFlux)
                .expectErrorMatches(throwable -> throwable instanceof OrderNotFoundException &&
                    throwable.getMessage().contains("No orders found for customer with document: " + document))
                .verify();

        verify(orderRepositoryPort, times(1)).findByCustomerDocumentNumber(document);
    }

    @Test
    void shouldEmitErrorWhenDocumentIsNullReactive(){
        Flux<Order> resultFlux = useCase.getOrdersByCustomerDocumentReactive(null);

        StepVerifier.create(resultFlux)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().equals("Customer document number cannot be null or empty"))
                .verify();

        verifyNoInteractions(orderRepositoryPort);
    }

    @Test
    void shouldEmitErrorWhenDocumentIsBlankReactive(){
        Flux<Order> resultFlux = useCase.getOrdersByCustomerDocumentReactive("    ");

        StepVerifier.create(resultFlux)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().equals("Customer document number cannot be null or empty"))
                .verify();

        verifyNoInteractions(orderRepositoryPort);
    }

    @Test
    void shouldEmitErrorWhenDocumentIsInvalidPatternReactive(){
        Flux<Order> resultFluxShort = useCase.getOrdersByCustomerDocumentReactive("123");

        StepVerifier.create(resultFluxShort)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("must be numeric and between 5 and 12 digits"))
                .verify();

        Flux<Order> resultFluxLong = useCase.getOrdersByCustomerDocumentReactive("1234567890123");

        StepVerifier.create(resultFluxLong)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("must be numeric and between 5 and 12 digits"))
                .verify();

        verifyNoInteractions(orderRepositoryPort);
    }
}
