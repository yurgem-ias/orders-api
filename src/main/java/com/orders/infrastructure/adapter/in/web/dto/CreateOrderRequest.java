package com.orders.infrastructure.adapter.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Purchase order creation request form")
public class CreateOrderRequest {

    @NotNull(message = "Customer details are required")
    @Valid
    @Schema(description = "buyer details and information", requiredMode = Schema.RequiredMode.REQUIRED)
    private CustomerDto customer;
    
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    @Schema(description = "List of items and products included in the order", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OrderItemDto> items;
}
