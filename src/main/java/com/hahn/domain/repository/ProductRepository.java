package com.hahn.domain.repository;

import com.hahn.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findActiveProducts();
    void deleteById(Long id);
}
