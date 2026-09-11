package com.example.springecom.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String mess) {
        super(mess);
    }
}