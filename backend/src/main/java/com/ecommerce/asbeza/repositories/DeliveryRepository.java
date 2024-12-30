package com.ecommerce.asbeza.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.asbeza.models.Delivery;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> { }
