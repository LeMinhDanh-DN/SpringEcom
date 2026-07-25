package com.example.springecom.model.dto.cart;

public record AddToCartRequest(
        int productId,
        int quantity
) {
}
