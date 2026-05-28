package com.example.springecom.model.dto;
public record AuthRequest(
        String email,
        String password
) {
}