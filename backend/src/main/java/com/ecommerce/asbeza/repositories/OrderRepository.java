package com.ecommerce.asbeza.repositories;

import com.ecommerce.asbeza.models.Order;
import com.ecommerce.asbeza.models.User;
import com.ecommerce.asbeza.types.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find orders by user
    @Query("SELECT o FROM Order o WHERE o.user = :user ORDER BY o.createdAt DESC")
    List<Order> findByUser(User user);

    // Find orders by status
    List<Order> findByStatus(OrderStatus status);

    // Find a specific order by ID and user (for security checks)
    Optional<Order> findByIdAndUser(Long id, User user);

    List<Order> findByDeliveryPersonnel(User deliveryPersonnel);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.deliveryPersonnel = :deliveryPersonnel")
    int countByDeliveryPersonnel(@Param("deliveryPersonnel") User deliveryPersonnel);

}
