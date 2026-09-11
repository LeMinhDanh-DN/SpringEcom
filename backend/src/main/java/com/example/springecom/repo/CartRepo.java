package com.example.springecom.repo;

import com.example.springecom.model.Cart;
import com.example.springecom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepo extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
