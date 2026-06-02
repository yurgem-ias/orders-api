package com.orders.infrastructure.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "response structure with the processed purchase order data")
public class OrderResponse {

    @Schema(description = "unique identifier assigned to the order")
    private String id;

    @Schema(description = "buyer details and information")
    private CustomerDto customer;

    @Schema(description = "List of items and products included in the order")
    private List<OrderItemDto> items;

    @Schema(description = "Total cost calculated for the order, applying any applicable discounts")
    private Double totalAmount;

    @Schema(description = "Date and time of the order creation on the server")
    private LocalDateTime createdAt;
}
