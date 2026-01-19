package com.example.inclassassignment.controller;

import com.example.inclassassignment.dto.AddToCartRequest;
import com.example.inclassassignment.dto.CartItemResponse;
import com.example.inclassassignment.model.CartItem;
import com.example.inclassassignment.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(@RequestBody AddToCartRequest request) {
        try {
            CartItem cartItem = cartService.addToCart(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItemResponse>> getCart(@PathVariable String userId) {
        try {
            List<CartItemResponse> cartItems = cartService.getCartByUserId(userId);
            return ResponseEntity.ok(cartItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<?> clearCart(@PathVariable String userId) {
        try {
            cartService.clearCart(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cart cleared successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
