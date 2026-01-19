package com.example.inclassassignment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private String id;
    private String userId;
    private String productId;
    private Integer quantity;
    private ProductResponse product;
}
