package com.example.springecom.controller;

import com.example.springecom.model.dto.OrderRequest;
import com.example.springecom.model.dto.OrderResponse;
import com.example.springecom.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private OrderService service;

    @PostMapping("/orders/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest,
                                                    @AuthenticationPrincipal UserDetails userDetails) {

        OrderResponse orderResponse = service.placeOrder(orderRequest, userDetails);
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders(@AuthenticationPrincipal UserDetails userDetails){
        List<OrderResponse> responses = service.getAllOrderResponses(userDetails);

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}
