package com.ecommerce.kafka.controller;

import com.ecommerce.kafka.dto.OrderRequest;
import com.ecommerce.kafka.dto.OrderResponse;
import com.ecommerce.kafka.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping
    public List<OrderResponse> getOrders() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Map<String, String>> payOrder(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("message", orderService.pay(id)));
    }
}
