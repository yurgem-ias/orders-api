package com.orders.infrastructure.adapter.out.persistence;

import com.orders.domain.model.Customer;
import com.orders.domain.model.Order;
import com.orders.domain.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

class InMemoryOrderRespositoryAdapaterTest {

    private InMemoryOrderRepositoryAdapter repository;
    private Order sampleOrder;

    @BeforeEach
    public void setUp(){
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
        StepVerifier.create(repository.save(sampleOrder))
                .expectNextMatches(saved -> saved.getId().equals("order-uuid-123") && saved.getTotalAmount() == 6000.0)
                .verifyComplete();

        StepVerifier.create(repository.findById("order-uuid-123"))
                .expectNextMatches(found -> found.getCustomer().getName().equals("Yurgen Prado") && found.getItems().size() == 1)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFound(){
        StepVerifier.create(repository.findById("non-existent-uuid"))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenIdIsNull(){
        StepVerifier.create(repository.findById(null))
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenSavingNullOrder(){
        StepVerifier.create(repository.save(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void shouldReturnErrorWhenSavingOrderWithNullId(){
        sampleOrder.setId(null);
        StepVerifier.create(repository.save(sampleOrder))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void shouldFindOrdersByCustomerDocumentNumber(){
        StepVerifier.create(repository.save(sampleOrder)).expectNextCount(1).verifyComplete();

        StepVerifier.create(repository.findByCustomerDocumentNumber("109213121"))
                .expectNextMatches(order -> order.getId().equals("order-uuid-123"))
                .verifyComplete();

        StepVerifier.create(repository.findByCustomerDocumentNumber("00000000"))
                .verifyComplete();

        StepVerifier.create(repository.findByCustomerDocumentNumber(null))
                .verifyComplete();
    }
}