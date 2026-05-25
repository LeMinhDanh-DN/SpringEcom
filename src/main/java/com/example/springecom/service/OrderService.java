package com.example.springecom.service;

import com.example.springecom.exception.ProductNotFoundException;
import com.example.springecom.model.Order;
import com.example.springecom.model.OrderItem;
import com.example.springecom.model.Product;
import com.example.springecom.model.dto.OrderItemResponse;
import com.example.springecom.model.dto.OrderRequest;
import com.example.springecom.model.dto.OrderResponse;
import com.example.springecom.repo.OrderRepo;
import com.example.springecom.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    OrderRepo orderRepo;
    @Autowired
    ProductRepo productRepo;

    public OrderResponse placeOrder(OrderRequest request){

        //Creating order obj for repostory
        Order order = new Order();
        String orderId = "ORD" + UUID.randomUUID().toString().substring(0,8).toUpperCase();

        order.setOrderId(orderId);
        order.setCustomerName(request.customerName());
        order.setEmail(request.email());
        order.setStatus("PLACED");
        order.setOrderDate(LocalDate.now());

        List<OrderItem> orderItems = new ArrayList<>();

        request.items().forEach(i -> {
            Product product = productRepo.findById(i.productId())
                    .orElseThrow(() -> new ProductNotFoundException("cant find product with id " + i.productId()));
            product.setStockQuantity(product.getStockQuantity() - i.quantity());
            productRepo.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(i.quantity())
                    .totalPrice(
                            product.getPrice()
                                    .multiply(BigDecimal.valueOf(i.quantity()))
                    )
                    .order(order)
                    .build();

            orderItems.add(orderItem);

        });

        order.setItems(orderItems);
        //already cascade at order so orderItem will be automatically saved
        Order savedOrder = orderRepo.save(order);


        //Creating orderResponse for Responsing to client
        List<OrderItemResponse> orderItemResponse= new ArrayList<>();
        savedOrder.getItems().forEach(orderItem -> {
            OrderItemResponse orderResponse = OrderItemResponse.builder()
                    .productName(orderItem.getProduct().getName())
                    .quantity(orderItem.getQuantity())
                    .totalPrice(orderItem.getTotalPrice())
                    .build();
            orderItemResponse.add(orderResponse);
        });
        OrderResponse orderResponse = new OrderResponse(
                savedOrder.getOrderId(),
                savedOrder.getCustomerName(),
                savedOrder.getEmail(),
                savedOrder.getStatus(),
                savedOrder.getOrderDate(),
                orderItemResponse);

        return orderResponse;
    }

    public List<OrderResponse> getAllOrderResponses(){
        List<Order> orders = orderRepo.findAll();
        List<OrderResponse> orderResponses = new ArrayList<>();

        for (Order order : orders) {
            List<OrderItemResponse> orderItemResponses = new ArrayList<>();

            order.getItems().forEach(orderItem -> {;
                OrderItemResponse orderItemResponse = OrderItemResponse.builder()
                        .productName(orderItem.getProduct().getName())
                        .quantity(orderItem.getQuantity())
                        .totalPrice(orderItem.getTotalPrice())
                        .build();
                orderItemResponses.add(orderItemResponse);
            });
            // Convert each Order to OrderResponse
            OrderResponse orderResponse = new OrderResponse(
                    order.getOrderId(),
                    order.getCustomerName(),
                    order.getEmail(),
                    order.getStatus(),
                    order.getOrderDate(),
                    orderItemResponses
            );
            orderResponses.add(orderResponse);
        }
        return orderResponses;
    }



}
