package com.example.inclassassignment.service;

import com.example.inclassassignment.dto.CreateOrderRequest;
import com.example.inclassassignment.dto.OrderItemResponse;
import com.example.inclassassignment.dto.OrderResponse;
import com.example.inclassassignment.dto.PaymentResponse;
import com.example.inclassassignment.model.CartItem;
import com.example.inclassassignment.model.Order;
import com.example.inclassassignment.model.OrderItem;
import com.example.inclassassignment.model.Payment;
import com.example.inclassassignment.model.Product;
import com.example.inclassassignment.repository.CartRepository;
import com.example.inclassassignment.repository.OrderRepository;
import com.example.inclassassignment.repository.PaymentRepository;
import com.example.inclassassignment.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    public Order createOrder(CreateOrderRequest request) {
        String userId = request.getUserId();
        
        // Get cart items
        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        // Check stock and calculate total
        double totalAmount = 0;
        List<OrderItem> orderItems = new java.util.ArrayList<>();
        
        for (CartItem cartItem : cartItems) {
            Optional<Product> product = productRepository.findById(cartItem.getProductId());
            if (product.isEmpty()) {
                throw new RuntimeException("Product not found: " + cartItem.getProductId());
            }
            
            Product p = product.get();
            if (p.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + p.getName());
            }
            
            totalAmount += p.getPrice() * cartItem.getQuantity();
            
            OrderItem orderItem = new OrderItem();
            orderItem.setId(UUID.randomUUID().toString());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(p.getPrice());
            orderItems.add(orderItem);
        }
        
        // Create order
        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus("CREATED");
        order.setCreatedAt(Instant.now());
        order.setItems(orderItems);
        
        // Set order ID for order items
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
        }
        
        Order savedOrder = orderRepository.save(order);
        
        // Update product stock
        for (CartItem cartItem : cartItems) {
            Optional<Product> product = productRepository.findById(cartItem.getProductId());
            if (product.isPresent()) {
                Product p = product.get();
                p.setStock(p.getStock() - cartItem.getQuantity());
                productRepository.save(p);
            }
        }
        
        // Clear cart
        cartRepository.deleteByUserId(userId);
        
        return savedOrder;
    }
    
    public OrderResponse getOrderById(String orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            throw new RuntimeException("Order not found");
        }
        
        return convertToResponse(order.get());
    }
    
    public List<OrderResponse> getOrdersByUserId(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        
        // Convert order items
        List<OrderItemResponse> itemResponses = order.getItems().stream()
            .map(item -> new OrderItemResponse(item.getProductId(), item.getQuantity(), item.getPrice()))
            .collect(Collectors.toList());
        response.setItems(itemResponses);
        
        // Get payment info
        Payment payment = paymentRepository.findByOrderId(order.getId());
        if (payment != null) {
            PaymentResponse paymentResponse = new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getPaymentId()
            );
            response.setPayment(paymentResponse);
        }
        
        return response;
    }
    
    public void updateOrderStatus(String orderId, String status) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            Order o = order.get();
            o.setStatus(status);
            orderRepository.save(o);
        }
    }
}
