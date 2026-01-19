package com.example.inclassassignment.service;

import com.example.inclassassignment.model.Product;
import com.example.inclassassignment.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }
    
    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }
    
    public Product updateProduct(String id, Product product) {
        Optional<Product> existing = productRepository.findById(id);
        if (existing.isPresent()) {
            Product p = existing.get();
            if (product.getName() != null) p.setName(product.getName());
            if (product.getDescription() != null) p.setDescription(product.getDescription());
            if (product.getPrice() != null) p.setPrice(product.getPrice());
            if (product.getStock() != null) p.setStock(product.getStock());
            return productRepository.save(p);
        }
        return null;
    }
}
