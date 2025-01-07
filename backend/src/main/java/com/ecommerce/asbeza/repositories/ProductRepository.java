package com.ecommerce.asbeza.repositories;

import com.ecommerce.asbeza.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find product by name
    Optional<Product> findByName(String name);

    // Find products by category
    List<Product> findByCategory(String category);

    // Search products by name containing a keyword (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String keyword);
}
