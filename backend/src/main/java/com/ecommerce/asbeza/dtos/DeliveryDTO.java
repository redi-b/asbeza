package com.ecommerce.asbeza.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDTO {
    private Long deliveryId;
    private Long orderId;
    private String deliveryAddress;
    private LocalDate scheduledDate;
    private String deliveryStatus;
}
