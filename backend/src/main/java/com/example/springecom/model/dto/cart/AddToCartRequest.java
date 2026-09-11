package com.example.springecom.model.dto.cart;

import org.jetbrains.annotations.NotNull;

public record AddToCartRequest(
                @NotNull int productId,
                @NotNull int quantity) {

}
