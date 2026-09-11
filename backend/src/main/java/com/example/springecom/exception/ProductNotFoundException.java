package com.example.springecom.exception;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(String mess){
        super(mess);
    }
}
