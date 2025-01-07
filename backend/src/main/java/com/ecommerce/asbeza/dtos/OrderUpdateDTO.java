package com.ecommerce.asbeza.dtos;

import com.ecommerce.asbeza.types.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateDTO {

    @NotNull(message = "Order status cannot be null.")
    private OrderStatus status;
}
