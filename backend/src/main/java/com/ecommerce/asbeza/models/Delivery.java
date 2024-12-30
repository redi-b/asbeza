package com.ecommerce.asbeza.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

enum DeliveryStatus {
    Pending, Shipped, Delivered
}

@Entity
@Table(name = "deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {
    @Id
    private Long deliveryId;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private Order order;

    private String deliveryPersonnel;
    private DeliveryStatus deliveryStatus = DeliveryStatus.Pending;
}