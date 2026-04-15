package com.ecommerce.kafka.service;

import com.ecommerce.kafka.dto.CustomerDTO;
import com.ecommerce.kafka.model.Customer;
import com.ecommerce.kafka.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDTO> findAll() {
        return customerRepository.findAll().stream().map(this::toDto).toList();
    }

    public Customer findEntity(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Customer not found: " + id));
    }

    public CustomerDTO create(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        return toDto(customerRepository.save(customer));
    }

    public CustomerDTO toDto(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setCreatedAt(customer.getCreatedAt());
        return dto;
    }
}
