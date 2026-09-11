package com.example.springecom.controller;

import com.example.springecom.model.dto.cart.AddToCartRequest;
import com.example.springecom.model.dto.cart.CartResponse;
import com.example.springecom.model.dto.cart.UpdateCartItemRequest;
import com.example.springecom.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        CartResponse cartResponse = cartService.getCartResponse(userDetails);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(@RequestBody AddToCartRequest request,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        CartResponse cartResponse = cartService.addToCart(request, userDetails);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateCartItemQuantity(@PathVariable Long itemId,
                                                               @RequestBody UpdateCartItemRequest request,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        CartResponse cartResponse = cartService.updateCartItemQuantity(itemId, request, userDetails);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeFromCart(@PathVariable Long itemId,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        CartResponse cartResponse = cartService.removeFromCart(itemId, userDetails);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<CartResponse> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        CartResponse cartResponse = cartService.clearCart(userDetails);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }
}
