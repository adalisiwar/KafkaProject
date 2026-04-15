package com.ecommerce.kafka.service;

import com.ecommerce.kafka.dto.ProductDTO;
import com.ecommerce.kafka.model.Product;
import com.ecommerce.kafka.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream().map(this::toDto).toList();
    }

    public Product findEntity(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }

    public ProductDTO create(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        return toDto(productRepository.save(product));
    }

    public ProductDTO toDto(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        return dto;
    }
}
