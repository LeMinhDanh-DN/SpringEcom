package com.example.springecom.model.dto.auth;

import org.jetbrains.annotations.NotNull;

public record RegisterRequest(
                @NotNull String email,
                @NotNull String password,
                @NotNull String name) {
}
