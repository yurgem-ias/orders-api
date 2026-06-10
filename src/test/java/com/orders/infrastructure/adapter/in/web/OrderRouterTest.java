package com.orders.infrastructure.adapter.in.web;

import com.orders.domain.model.Customer;
import com.orders.domain.model.Order;
import com.orders.domain.model.OrderItem;
import com.orders.domain.port.in.CreateOrderUseCase;
import com.orders.domain.port.in.GetOrderUseCase;
import com.orders.domain.port.in.GetOrdersByCustomerUseCase;
import com.orders.infrastructure.adapter.in.web.dto.CreateOrderRequest;
import com.orders.infrastructure.adapter.in.web.dto.CustomerDto;
import com.orders.infrastructure.adapter.in.web.dto.OrderItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest({OrderRouter.class, OrderHandler.class, GlobalExceptionHandler.class})
public class OrderRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CreateOrderUseCase createOrderUseCase;

    @MockitoBean
    private GetOrderUseCase getOrderUseCase;

    @MockitoBean
    private GetOrdersByCustomerUseCase getOrdersByCustomerUseCase;

    private CreateOrderRequest validRequest;
    private Order mockSavedOrder;

    @BeforeEach
    void setUp() {
        CustomerDto customerDto = CustomerDto.builder()
                .name("Yurgen Alvarez")
                .email("yurgen@example.com")
                .documentType("CC")
                .documentNumber("123456789")
                .build();

        OrderItemDto itemDto = OrderItemDto.builder()
                .productId("PROD-1")
                .quantity(3)
                .unitPrice(10.0)
                .build();

        validRequest = CreateOrderRequest.builder()
                .customer(customerDto)
                .items(List.of(itemDto))
                .build();

        mockSavedOrder = Order.builder()
                .id("some-uuid")
                .customer(Customer.builder()
                        .name("Yurgen Alvarez")
                        .email("yurgen@example.com")
                        .documentType("CC")
                        .documentNumber("123456789")
                        .build())
                .items(List.of(OrderItem.builder()
                        .productId("PROD-1")
                        .quantity(3)
                        .unitPrice(10.0)
                        .build()))
                .totalAmount(30.0)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void shouldCreateOrderWhenPayloadIsValid() {
        when(createOrderUseCase.createOrder(any(Order.class))).thenReturn(Mono.just(mockSavedOrder));

        webTestClient.post().uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("some-uuid")
                .jsonPath("$.customer.name").isEqualTo("Yurgen Alvarez")
                .jsonPath("$.totalAmount").isEqualTo(30.0)
                .jsonPath("$.items").isArray()
                .jsonPath("$.items[0].productId").isEqualTo("PROD-1");
    }

    @Test
    public void shouldReturnBadRequestWhenPayloadIsInvalid() {
        CustomerDto customerDto = CustomerDto.builder()
                .name("Yurgen Alvarez")
                .email("invalid-email")
                .documentType("CC")
                .documentNumber("123") 
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .customer(customerDto)
                .items(Collections.emptyList())
                .build();

        webTestClient.post().uri("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").value(containsString("Validation failed"))
                .jsonPath("$.validationErrors").isArray();
    }

    @Test
    public void shouldReturnOrderWhenExists() {
        when(getOrderUseCase.getOrderById("12345")).thenReturn(Mono.just(mockSavedOrder));

        webTestClient.get().uri("/api/v1/orders/12345")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("some-uuid")
                .jsonPath("$.customer.name").isEqualTo("Yurgen Alvarez");
    }

    @Test
    public void shouldReturnNotFoundWhenOrderDoesNotExist() {
        when(getOrderUseCase.getOrderById("non-existent")).thenReturn(Mono.empty());

        webTestClient.get().uri("/api/v1/orders/non-existent")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").value(containsString("Order with ID non-existent not found"));
    }

    @Test
    public void shouldReturnOrdersByCustomerWhenValid() {
        String document = "123456789";
        when(getOrdersByCustomerUseCase.getOrdersByCustomerDocument(document))
                .thenReturn(Flux.just(mockSavedOrder));

        webTestClient.get().uri("/api/v1/orders/customer/" + document)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].id").isEqualTo("some-uuid")
                .jsonPath("$[0].customer.documentNumber").isEqualTo(document);
    }

    @Test
    public void shouldReturnBadRequestWhenCustomerDocumentIsInvalid() {
        webTestClient.get().uri("/api/v1/orders/customer/123")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").value(containsString("Customer document number must be numeric and between 5 and 12 digits"));
    }
}