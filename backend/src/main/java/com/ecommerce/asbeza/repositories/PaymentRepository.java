package com.ecommerce.asbeza.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.asbeza.models.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> { }
