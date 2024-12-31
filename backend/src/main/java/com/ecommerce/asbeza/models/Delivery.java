package com.ecommerce.asbeza.models;

import com.ecommerce.asbeza.types.DeliveryStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    private LocalDate scheduledDate;
    private String deliveryAddress;
    private String deliveryPersonnel;
    private DeliveryStatus deliveryStatus = DeliveryStatus.Pending;
}