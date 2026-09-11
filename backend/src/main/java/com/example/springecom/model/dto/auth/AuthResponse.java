package com.example.springecom.model.dto.auth;

public record AuthResponse(
        String token,
        UserResponse user) {

}
