package com.example.springecom.service;

import com.example.springecom.exception.ProductNotFoundException;
import com.example.springecom.model.Cart;
import com.example.springecom.model.CartItem;
import com.example.springecom.model.Product;
import com.example.springecom.model.User;
import com.example.springecom.model.dto.cart.AddToCartRequest;
import com.example.springecom.model.dto.cart.CartItemResponse;
import com.example.springecom.model.dto.cart.CartResponse;
import com.example.springecom.model.dto.cart.UpdateCartItemRequest;
import com.example.springecom.repo.CartItemRepo;
import com.example.springecom.repo.CartRepo;
import com.example.springecom.repo.ProductRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private UserService userService;

    public Cart getOrCreateCart(User user) {
        return cartRepo.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepo.save(newCart);
        });
    }

    public CartResponse getCartResponse(UserDetails userDetails) {
        User user = userService.findByUserName(userDetails.getUsername());
        Cart cart = getOrCreateCart(user);
        return mapToCartResponse(cart);
    }

    public CartResponse addToCart(AddToCartRequest request, UserDetails userDetails) {
        if (request.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        User user = userService.findByUserName(userDetails.getUsername());
        Cart cart = getOrCreateCart(user);

        Product product = productRepo.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException("Cannot find product with id " + request.productId()));

        // this product already exist in cart or not
        Optional<CartItem> existingCartItem = cartItemRepo.findByCartAndProduct(cart, product);
        int newQuantity = request.quantity();
        if (existingCartItem.isPresent()) {
            newQuantity += existingCartItem.get().getQuantity();
        }

        // Check stock
        if (product.getStockQuantity() < newQuantity) {
            throw new IllegalArgumentException("Insufficient stock for product " + product.getName() + ". Available: "
                    + product.getStockQuantity());
        }

        if (existingCartItem.isPresent()) {
            CartItem item = existingCartItem.get();
            item.setQuantity(newQuantity);
            item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
            cartItemRepo.save(item);
        }

        else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(newQuantity);
            newItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
            cart.getCartItems().add(newItem);
            cartItemRepo.save(newItem);
        }

        return mapToCartResponse(cartRepo.save(cart));
    }

    public CartResponse updateCartItemQuantity(Long itemId, UpdateCartItemRequest request, UserDetails userDetails) {
        User user = userService.findByUserName(userDetails.getUsername());
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to user's cart");
        }

        if (request.quantity() <= 0) {
            cart.getCartItems().remove(item);
            cartItemRepo.delete(item);
        }

        else {
            if (item.getProduct().getStockQuantity() < request.quantity()) {
                throw new IllegalArgumentException("Insufficient stock for product " + item.getProduct().getName()
                        + ". Available: " + item.getProduct().getStockQuantity());
            }
            item.setQuantity(request.quantity());
            item.setTotalPrice(item.getProduct().getPrice().multiply(BigDecimal.valueOf(request.quantity())));
            cartItemRepo.save(item);
        }

        return mapToCartResponse(cartRepo.save(cart));
    }

    public CartResponse removeFromCart(Long itemId, UserDetails userDetails) {
        User user = userService.findByUserName(userDetails.getUsername());
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id " + itemId));

        if (item.getCart().getId().equals(cart.getId())) {
            cart.getCartItems().remove(item);
            cartItemRepo.delete(item);
        }

        return mapToCartResponse(cartRepo.save(cart));
    }

    public CartResponse clearCart(UserDetails userDetails) {
        User user = userService.findByUserName(userDetails.getUsername());
        Cart cart = getOrCreateCart(user);

        cart.getCartItems().clear();
        return mapToCartResponse(cartRepo.save(cart));
    }

    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal totalCartPrice = BigDecimal.ZERO;

        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();
            BigDecimal itemTotalPrice = item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO;
            totalCartPrice = totalCartPrice.add(itemTotalPrice);

            CartItemResponse itemResponse = CartItemResponse.builder()
                    .id(item.getId())
                    .productId(product.getId())
                    .productName(product.getName())
                    .brand(product.getBrand())
                    .price(product.getPrice())
                    .quantity(item.getQuantity())
                    .totalPrice(itemTotalPrice)
                    .imageName(product.getImageName())
                    .imageType(product.getImageType())
                    .build();

            itemResponses.add(itemResponse);
        }

        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .totalCartPrice(totalCartPrice)
                .build();
    }

    public Cart findUserCart(User user) {
        return getOrCreateCart(user);
    }
}
