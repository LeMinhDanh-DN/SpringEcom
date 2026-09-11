package com.example.springecom.model.dto.auth;

import org.jetbrains.annotations.NotNull;

public record AuthRequest(
                @NotNull String email,
                @NotNull String password) {
}