package com.example.springecom.model.dto;

public record UserResponse(
        int id,
        String username,
        String email,
        String name) {
}