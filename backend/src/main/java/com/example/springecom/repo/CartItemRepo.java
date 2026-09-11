package com.example.springecom.repo;

import com.example.springecom.model.Cart;
import com.example.springecom.model.CartItem;
import com.example.springecom.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepo extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
