package com.ecommerce.kafka.repository;

import com.ecommerce.kafka.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
