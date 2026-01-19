package com.example.inclassassignment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String id;
    private String userId;
    private Double totalAmount;
    private String status;
    private List<OrderItemResponse> items;
    private PaymentResponse payment;
}
