package com.example.springecom.model.dto.cart;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CartResponse(
        Long id,
        List<CartItemResponse> items,
        BigDecimal totalCartPrice
) {
}
