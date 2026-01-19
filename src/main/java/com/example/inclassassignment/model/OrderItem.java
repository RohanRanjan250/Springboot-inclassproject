package com.example.inclassassignment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private String id;
    private String orderId;
    private String productId;
    private Integer quantity;
    private Double price;
}
