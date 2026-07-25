package com.example.springecom.service;

import com.example.springecom.model.*;
import com.example.springecom.model.dto.order.OrderItemResponse;
import com.example.springecom.model.dto.order.OrderRequest;
import com.example.springecom.model.dto.order.OrderResponse;
import com.example.springecom.repo.OrderRepo;
import com.example.springecom.repo.ProductRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;

    public OrderResponse placeOrder(OrderRequest request, UserDetails userDetails) {
        User user = userService.findByUserName(userDetails.getUsername());
        Cart cart = cartService.findUserCart(user);
        List<CartItem> cartItems = cart.getCartItems();

        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalStateException("Your cart is empty.");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        Order order = new Order();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException("Product '" + product.getName() + "' is out of stock (remaining: "
                        + product.getStockQuantity() + ")");
            }

            int updatedStock = product.getStockQuantity() - cartItem.getQuantity();
            product.setStockQuantity(updatedStock);
            if (updatedStock == 0) {
                product.setProductAvailable(false);
            }
            productRepo.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .totalPrice(cartItem.getTotalPrice())
                    .order(order)
                    .build();

            orderItems.add(orderItem);
        }

        order.setOrderId(UUID.randomUUID().toString());
        order.setCustomerName(request.customerName());
        order.setEmail(request.email());
        order.setNumber(request.number());
        order.setShippingAddress(request.shippingAddress());
        order.setPayMethod(request.payMethod());
        order.setStatus("PLACED");
        order.setOrderDate(LocalDate.now());
        order.setUser(user);
        order.setItems(orderItems);

        Order savedOrder = orderRepo.save(order);

        cartService.clearCart(userDetails);

        return mapToOrderResponse(savedOrder);
    }

    public List<OrderResponse> getAllOrderResponses(UserDetails userDetails) {
        User user = userService.findByUserName(userDetails.getUsername());
        List<Order> orders = orderRepo.findByUser(user);

        return orders.stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::mapToOrderItemResponse)
                .toList();

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .customerName(order.getCustomerName())
                .email(order.getEmail())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .number(order.getNumber())
                .shippingAddress(order.getShippingAddress())
                .payMethod(order.getPayMethod())
                .items(itemResponses)
                .build();
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .productName(orderItem.getProduct().getName())
                .quantity(orderItem.getQuantity())
                .totalPrice(orderItem.getTotalPrice())
                .build();
    }

}
