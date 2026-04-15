package com.ecommerce.kafka.repository;

import com.ecommerce.kafka.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Override
    @EntityGraph(attributePaths = {"customer", "items", "items.product"})
    List<Order> findAll();

    @EntityGraph(attributePaths = {"customer", "items", "items.product"})
    Optional<Order> findWithDetailsById(Long id);
}
