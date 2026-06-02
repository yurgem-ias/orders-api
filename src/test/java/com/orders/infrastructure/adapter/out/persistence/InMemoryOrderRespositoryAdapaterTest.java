package com.orders.infrastructure.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orders.domain.model.Customer;
import com.orders.domain.model.Order;
import com.orders.domain.model.OrderItem;

class InMemoryOrderRespositoryAdapaterTest {

    private InMemoryOrderRepositoryAdapter repository;
    private Order sampleOrder;

    @BeforeEach
    void setUp(){
        repository = new InMemoryOrderRepositoryAdapter();

        Customer customer = Customer.builder()
                    .name("Yurgen Prado")
                    .email("yurgen.prado@ias.com.co")
                    .documentType("CC")
                    .documentNumber("109213121")
                    .build();

        OrderItem item = OrderItem.builder()
                    .productId("PROD-001")
                    .quantity(2)
                    .unitPrice(3000.0)
                    .build();

        sampleOrder = Order.builder()
                    .id("order-uuid-123")
                    .customer(customer)
                    .items(List.of(item))
                    .totalAmount(6000.0)
                    .createdAt(LocalDateTime.now())
                    .build();
    }

    @Test
    void shouldSaveAndRetrieveOrderSuccessfully(){
        Order saveOrder = repository.save(sampleOrder);
        assertNotNull(saveOrder);
        assertEquals("order-uuid-123", saveOrder.getId());

        Optional<Order> foundOrderOpt = repository.findById("order-uuid-123");
        assertTrue(foundOrderOpt.isPresent());
        Order foundOrder = foundOrderOpt.get();
        assertEquals("Yurgen Prado", foundOrder.getCustomer().getName());
        assertEquals(6000.0, foundOrder.getTotalAmount());
        assertEquals(1, foundOrder.getItems().size());
    }

    @Test
    void shouldReturnEmptyOptionalWhenOrderNotFound(){
        Optional<Order> foundOrderOpt = repository.findById("non-existent-uuid");
        assertFalse(foundOrderOpt.isPresent());
    }

    @Test
    void shouldReturnEmptyOptionalWhenIdIsNull(){
        Optional<Order> foundOrderOpt = repository.findById(null);
        assertFalse(foundOrderOpt.isPresent());
    }

    @Test
    void shouldReturnEmptyOptionalWhenSavingNullOrder(){
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    @Test
    void shouldReturnEmptyOptionalWhenSavingOrderWithNullId(){
        sampleOrder.setId(null);
        assertThrows(IllegalArgumentException.class, () -> repository.save(sampleOrder));
    }

    @Test
    void shouldFindOrdersByCustomerDocumentNumber(){
        repository.save(sampleOrder);

        List<Order> orders = repository.findByCustomerDocumentNumber("12345678");
        assertEquals(1, orders.size());
        assertEquals("order-uuid-123", orders.get(0).getId());

        List<Order> notFound = repository.findByCustomerDocumentNumber("00000000");
        assertTrue(notFound.isEmpty());

        List<Order> nullDocument = repository.findByCustomerDocumentNumber(null);
        assertTrue(nullDocument.isEmpty());
    }
}
