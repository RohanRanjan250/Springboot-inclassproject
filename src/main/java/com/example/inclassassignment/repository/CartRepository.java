package com.example.inclassassignment.repository;

import com.example.inclassassignment.model.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends MongoRepository<CartItem, String> {
    List<CartItem> findByUserId(String userId);
    void deleteByUserIdAndProductId(String userId, String productId);
    void deleteByUserId(String userId);
    CartItem findByUserIdAndProductId(String userId, String productId);
}
