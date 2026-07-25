package com.example.springecom.model.dto.cart;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CartItemResponse(
        Long id,
        int productId,
        String productName,
        String brand,
        BigDecimal price,
        int quantity,
        BigDecimal totalPrice,
        String imageName,
        String imageType
) {
}
