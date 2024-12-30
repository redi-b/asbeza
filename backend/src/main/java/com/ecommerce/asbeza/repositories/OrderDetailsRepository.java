package com.ecommerce.asbeza.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.asbeza.models.OrderDetails;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> { }
