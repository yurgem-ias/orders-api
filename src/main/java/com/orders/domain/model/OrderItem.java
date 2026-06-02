package com.orders.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private String productId;
    private Integer quantity;
    private Double unitPrice;

    public Double getSubTotal() {
        if (quantity == null || unitPrice == null) {
            return 0.0;
        }
        return quantity * unitPrice;
    }
}
