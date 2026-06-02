package com.orders.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.orders.domain.model.Customer;
import com.orders.domain.model.Order;
import com.orders.domain.port.out.OrderRepositoryPort;

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
                .customer(Customer.builder().documentNumber(document).name("Yurgen Prado").build())
                .build();

        when(orderRepositoryPort.findByCustomerDocumentNumber(document)).thenReturn(List.of(order));

        List<Order> results = useCase.getOrdersByCustomerDocument(document);

        assertEquals(1, results.size());
        assertEquals("order-id-1", results.get(0).getId());
        verify(orderRepositoryPort, times(1)).findByCustomerDocumentNumber(document);
    }

    @Test
    void shouldThrowExceptionWhenDocumentIsNull(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()->{
            useCase.getOrdersByCustomerDocument(null);
        });
        assertEquals("Customer document number cannot be null or empty", exception.getMessage());
        verifyNoInteractions(orderRepositoryPort);
    }

    @Test
    void shouldThrowExceptionWhenDocumentIsBlank(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()->{
            useCase.getOrdersByCustomerDocument("    ");
        });
        assertEquals("Customer document number cannot be null or empty", exception.getMessage());
        verifyNoInteractions(orderRepositoryPort);
    }

    @Test
    void shouldThrowExceptionWhenDocumentIsInvalidPattern(){
        IllegalArgumentException exceptionShort = assertThrows(IllegalArgumentException.class, ()->{
            useCase.getOrdersByCustomerDocument("123");
        });
        assertTrue(exceptionShort.getMessage().contains("must be numeric and between 5 and 12 digits"));

        IllegalArgumentException exceptionLong = assertThrows(IllegalArgumentException.class, ()->{
            useCase.getOrdersByCustomerDocument("1234567890123");
        });
        assertTrue(exceptionLong.getMessage().contains("must be numeric and between 5 and 12 digits"));

        IllegalArgumentException exceptionCharapter = assertThrows(IllegalArgumentException.class, ()->{
            useCase.getOrdersByCustomerDocument("123asdas321");
        });
        assertTrue(exceptionCharapter.getMessage().contains("must be numeric and between 5 and 12 digits"));

        verifyNoInteractions(orderRepositoryPort);

    }
}
