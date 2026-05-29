package com.example.springecom.repo;

import com.example.springecom.model.Order;
import com.example.springecom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Integer> {

    List<Order> findByUser(User user);

}
