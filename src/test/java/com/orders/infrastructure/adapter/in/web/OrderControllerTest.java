package com.orders.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orders.domain.model.Customer;
import com.orders.domain.model.Order;
import com.orders.domain.model.OrderItem;
import com.orders.domain.port.in.CreateOrderUseCase;
import com.orders.domain.port.in.GetOrderUseCase;
import com.orders.infrastructure.adapter.in.web.dto.CreateOrderRequest;
import com.orders.infrastructure.adapter.in.web.dto.CustomerDto;
import com.orders.infrastructure.adapter.in.web.dto.OrderItemDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateOrderUseCase createOrderUseCase;

    @MockitoBean
    private GetOrderUseCase getOrderUseCase;

    @Test
    public void shouldCreateOrderWhenPayloadIsValid() throws Exception {
        // Arrange
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

        CreateOrderRequest request = CreateOrderRequest.builder()
                .customer(customerDto)
                .items(List.of(itemDto))
                .build();

        Order mockSavedOrder = Order.builder()
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

        when(createOrderUseCase.createOrder(any(Order.class))).thenReturn(mockSavedOrder);

        // Act & Assert
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("some-uuid")))
                .andExpect(jsonPath("$.customer.name", is("Yurgen Alvarez")))
                .andExpect(jsonPath("$.totalAmount", is(30.0)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId", is("PROD-1")));
    }

    @Test
    public void shouldReturnBadRequestWhenPayloadIsInvalid() throws Exception {
        // Arrange (invalid email, empty items list, short doc number)
        CustomerDto customerDto = CustomerDto.builder()
                .name("Yurgen Alvarez")
                .email("invalid-email")
                .documentType("CC")
                .documentNumber("123") // too short, requires 5-12 digits
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .customer(customerDto)
                .items(Collections.emptyList())
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Validation failed")))
                .andExpect(jsonPath("$.validationErrors", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    public void shouldReturnOrderWhenExists() throws Exception {
        // Arrange
        Order mockOrder = Order.builder()
                .id("12345")
                .customer(Customer.builder()
                        .name("John Doe")
                        .email("john@example.com")
                        .documentType("CC")
                        .documentNumber("987654321")
                        .build())
                .items(List.of(OrderItem.builder()
                        .productId("P1")
                        .quantity(1)
                        .unitPrice(100.0)
                        .build()))
                .totalAmount(100.0)
                .createdAt(LocalDateTime.now())
                .build();

        when(getOrderUseCase.getOrderById("12345")).thenReturn(Optional.of(mockOrder));

        // Act & Assert
        mockMvc.perform(get("/api/v1/orders/12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("12345")))
                .andExpect(jsonPath("$.customer.name", is("John Doe")))
                .andExpect(jsonPath("$.totalAmount", is(100.0)));
    }

    @Test
    public void shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        // Arrange
        when(getOrderUseCase.getOrderById("non-existent")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/orders/non-existent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Order with ID non-existent not found")));
    }
}
