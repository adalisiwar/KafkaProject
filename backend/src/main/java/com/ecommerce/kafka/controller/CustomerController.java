package com.ecommerce.kafka.controller;

import com.ecommerce.kafka.dto.CustomerDTO;
import com.ecommerce.kafka.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerDTO> getCustomers() {
        return customerService.findAll();
    }

    @PostMapping
    public CustomerDTO createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        return customerService.create(customerDTO);
    }
}
