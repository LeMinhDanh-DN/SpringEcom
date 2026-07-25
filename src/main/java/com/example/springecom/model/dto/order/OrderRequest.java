package com.example.springecom.model.dto.order;

import java.util.List;

public record OrderRequest(
        String customerName,
        String email,
        List<OrderItemResquest> items
) {
}
