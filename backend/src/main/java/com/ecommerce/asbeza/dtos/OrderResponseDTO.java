package com.ecommerce.asbeza.dtos;

import com.ecommerce.asbeza.types.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {

    private Long id;

    private Long userId;

    private List<OrderItemResponseDTO> items;

    private double totalPrice;

    private OrderStatus status;

    private String address;

    private String phoneNumber;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
