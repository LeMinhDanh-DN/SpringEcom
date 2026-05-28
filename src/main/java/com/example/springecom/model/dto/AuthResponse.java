package com.example.springecom.model.dto;

public record AuthResponse(
        String token,
        UserResponse user) {

}
