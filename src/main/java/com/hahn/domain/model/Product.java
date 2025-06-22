package com.hahn.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

    private Product() {}

    public static Product create(String name, String description, BigDecimal price, Integer quantity) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        LocalDateTime now = LocalDateTime.now();
        return new Product(null, name.trim(), description != null ? description.trim() : null,
                price, quantity, now, now, true);
    }

    public void updateDetails(String name, String description, BigDecimal price) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
        if (description != null) {
            this.description = description.trim();
        }
        if (price != null && price.compareTo(BigDecimal.ZERO) >= 0) {
            this.price = price;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void adjustQuantity(int delta) {
        if (this.quantity + delta < 0) {
            throw new IllegalStateException("Insufficient quantity");
        }
        this.quantity += delta;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public static Product reconstruct(
            Long id,
            String name,
            String description,
            BigDecimal price,
            Integer quantity,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            boolean active) {
        return new Product(id, name, description, price, quantity,
                createdAt, updatedAt, active);
    }
}
