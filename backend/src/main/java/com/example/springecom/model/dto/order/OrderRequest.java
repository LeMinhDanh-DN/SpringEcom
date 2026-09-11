package com.example.springecom.model.dto.order;

import org.jetbrains.annotations.NotNull;

public record OrderRequest(
                @NotNull String customerName,
                @NotNull String email,
                @NotNull String number,
                @NotNull String shippingAddress,
                @NotNull String payMethod,
                String voucherCode,
                String note) {
}
