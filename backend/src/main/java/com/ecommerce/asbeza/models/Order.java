package com.ecommerce.asbeza.models;

import com.ecommerce.asbeza.types.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<OrderItem> items = new ArrayList<>();

    @NotNull(message = "Total price cannot be null.")
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Order status cannot be null.")
    private OrderStatus status;

    @NotNull(message = "Address cannot be null.")
    private String address;

    @NotNull(message = "Contact number cannot be null.")
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "delivery_personnel_id")
    private User deliveryPersonnel;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
