package com.example.inclassassignment.service;

import com.example.inclassassignment.dto.AddToCartRequest;
import com.example.inclassassignment.dto.CartItemResponse;
import com.example.inclassassignment.model.CartItem;
import com.example.inclassassignment.model.Product;
import com.example.inclassassignment.repository.CartRepository;
import com.example.inclassassignment.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    public CartItem addToCart(AddToCartRequest request) {
        // Check if product exists
        Optional<Product> product = productRepository.findById(request.getProductId());
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        
        // Check if product already in cart
        CartItem existingItem = cartRepository.findByUserIdAndProductId(
            request.getUserId(), 
            request.getProductId()
        );
        
        if (existingItem != null) {
            // Update quantity
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            return cartRepository.save(existingItem);
        }
        
        // Create new cart item
        CartItem cartItem = new CartItem();
        cartItem.setId(UUID.randomUUID().toString());
        cartItem.setUserId(request.getUserId());
        cartItem.setProductId(request.getProductId());
        cartItem.setQuantity(request.getQuantity());
        
        return cartRepository.save(cartItem);
    }
    
    public List<CartItemResponse> getCartByUserId(String userId) {
        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        return cartItems.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    private CartItemResponse convertToResponse(CartItem cartItem) {
        Optional<Product> product = productRepository.findById(cartItem.getProductId());
        CartItemResponse response = new CartItemResponse();
        response.setId(cartItem.getId());
        response.setUserId(cartItem.getUserId());
        response.setProductId(cartItem.getProductId());
        response.setQuantity(cartItem.getQuantity());
        
        if (product.isPresent()) {
            Product p = product.get();
            response.setProduct(new com.example.inclassassignment.dto.ProductResponse(
                p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock()
            ));
        }
        
        return response;
    }
    
    public void clearCart(String userId) {
        cartRepository.deleteByUserId(userId);
    }
    
    public List<CartItem> getCartItems(String userId) {
        return cartRepository.findByUserId(userId);
    }
}
