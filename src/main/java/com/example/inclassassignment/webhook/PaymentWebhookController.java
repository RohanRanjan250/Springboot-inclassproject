package com.example.inclassassignment.webhook;

import com.example.inclassassignment.dto.PaymentWebhookRequest;
import com.example.inclassassignment.model.Payment;
import com.example.inclassassignment.repository.PaymentRepository;
import com.example.inclassassignment.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/webhooks")
@CrossOrigin(origins = "*")
public class PaymentWebhookController {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private OrderService orderService;
    
    @PostMapping("/payment")
    public ResponseEntity<?> handlePaymentWebhook(@RequestBody PaymentWebhookRequest request) {
        try {
            // Find payment by payment ID
            Optional<Payment> paymentOpt = paymentRepository.findById(request.getPaymentId());
            
            if (paymentOpt.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "Payment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            Payment payment = paymentOpt.get();
            
            // Update payment status
            payment.setStatus(request.getStatus());
            paymentRepository.save(payment);
            
            // Update order status based on payment status
            String orderStatus = "SUCCESS".equalsIgnoreCase(request.getStatus()) ? "PAID" : "FAILED";
            orderService.updateOrderStatus(payment.getOrderId(), orderStatus);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Payment webhook processed");
            response.put("orderId", payment.getOrderId());
            response.put("paymentStatus", request.getStatus());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
