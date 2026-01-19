package com.example.inclassassignment.service;

import com.example.inclassassignment.dto.PaymentRequest;
import com.example.inclassassignment.model.Order;
import com.example.inclassassignment.model.Payment;
import com.example.inclassassignment.repository.OrderRepository;
import com.example.inclassassignment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public Payment createPayment(PaymentRequest request) {
        Optional<Order> order = orderRepository.findById(request.getOrderId());
        if (order.isEmpty()) {
            throw new RuntimeException("Order not found");
        }
        
        Order o = order.get();
        if (!"CREATED".equals(o.getStatus())) {
            throw new RuntimeException("Order status must be CREATED");
        }
        
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID().toString());
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setStatus("PENDING");
        payment.setPaymentId("pay_" + UUID.randomUUID().toString().substring(0, 12));
        payment.setCreatedAt(Instant.now());
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Simulate mock payment service - call webhook after 3 seconds
        simulateMockPayment(savedPayment);
        
        return savedPayment;
    }
    
    @Async
    private void simulateMockPayment(Payment payment) {
        try {
            // Wait 3 seconds to simulate payment processing
            Thread.sleep(3000);
            
            // Update payment status to SUCCESS
            payment.setStatus("SUCCESS");
            paymentRepository.save(payment);
            
            // Update order status to PAID
            orderService.updateOrderStatus(payment.getOrderId(), "PAID");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
            orderService.updateOrderStatus(payment.getOrderId(), "FAILED");
        }
    }
    
    public Payment getPaymentById(String paymentId) {
        Optional<Payment> payment = paymentRepository.findById(paymentId);
        if (payment.isEmpty()) {
            throw new RuntimeException("Payment not found");
        }
        return payment.get();
    }
    
    public Payment getPaymentByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId);
        if (payment == null) {
            throw new RuntimeException("Payment not found for order: " + orderId);
        }
        return payment;
    }
}
