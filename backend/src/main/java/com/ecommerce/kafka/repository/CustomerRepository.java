package com.ecommerce.kafka.repository;

import com.ecommerce.kafka.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
