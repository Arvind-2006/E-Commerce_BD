package com.app.ecommerce.repository;

import com.app.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom query method that looks for matching strings case-insensitively
    List<Product> findByNameContainingIgnoreCase(String keyword);
}