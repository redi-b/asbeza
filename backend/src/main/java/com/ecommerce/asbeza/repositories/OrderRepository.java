package com.ecommerce.asbeza.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.asbeza.models.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByEmail(String email);
}
