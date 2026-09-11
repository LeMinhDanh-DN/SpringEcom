package com.example.springecom.model.dto.order;

import org.jetbrains.annotations.NotNull;

public record OrderItemResquest(
                @NotNull int productId,
                @NotNull int quantity) {

}
