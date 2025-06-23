package com.hahn.application.service;

import com.hahn.application.dto.ApiResponse;
import com.hahn.application.dto.product.CreateProductDto;
import com.hahn.application.dto.product.ProductDto;
import com.hahn.application.dto.product.UpdateProductDto;
import com.hahn.domain.exception.ResourceNotFoundException;
import com.hahn.domain.model.Product;
import com.hahn.domain.repository.ProductRepository;
import com.hahn.infrastructure.persistence.product.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductDto createProduct(CreateProductDto request) {
        Product product = Product.create(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getQuantity()
        );
        Product savedProduct = productRepository.save(product);
        return ProductMapper.toDto(savedProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productRepository.findActiveProducts().stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public ProductDto updateProduct(Long id, UpdateProductDto request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.updateDetails(
                request.getName(),
                request.getDescription(),
                request.getPrice()
        );

        if (request.getQuantity() != null) {
            int delta = request.getQuantity() - product.getQuantity();
            product.adjustQuantity(delta);
        }

        Product updatedProduct = productRepository.save(product);
        return ProductMapper.toDto(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.deactivate();
        productRepository.save(product);
    }
}