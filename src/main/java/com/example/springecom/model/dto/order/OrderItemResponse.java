package com.example.springecom.model.dto.order;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemResponse(
        String productName,
        int quantity,
        BigDecimal totalPrice
) {
}
