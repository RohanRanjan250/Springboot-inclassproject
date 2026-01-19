package com.example.inclassassignment.repository;

import com.example.inclassassignment.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    Payment findByOrderId(String orderId);
}
