package com.orders.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product details and quantity requested in the order")
public class OrderItemDto {

    @NotBlank(message = "Product ID is required")
    @Schema(description = "unique identifier or sku of the product", example = "PROD-001",  requiredMode = Schema.RequiredMode.REQUIRED)
    private String productId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than zero")
    @Schema(description = "quantity of product requested", example = "3",  requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;
    
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than zero")
    @Schema(description = "unit price of the product", example = "300.0",  requiredMode = Schema.RequiredMode.REQUIRED)
    private Double unitPrice;
}
