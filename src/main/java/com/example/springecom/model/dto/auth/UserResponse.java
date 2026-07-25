package com.example.springecom.model.dto.auth;

public record UserResponse(
        int id,
        String username,
        String email,
        String name,
        String role) {
}