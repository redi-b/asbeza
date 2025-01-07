package com.ecommerce.asbeza.repositories;

import com.ecommerce.asbeza.models.Cart;
import com.ecommerce.asbeza.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user); // Find cart by user

    boolean existsByUser(User user); // Check if cart exists for a user
}
