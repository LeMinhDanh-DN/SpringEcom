package com.example.springecom.model.dto;

public record AddToCartRequest(
        int productId,
        int quantity
) {
}
