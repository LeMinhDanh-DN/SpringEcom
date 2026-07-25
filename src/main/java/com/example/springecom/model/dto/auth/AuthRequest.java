package com.example.springecom.model.dto.auth;
public record AuthRequest(
        String email,
        String password
) {
}