package com.ecommerce.asbeza.dtos;

import com.ecommerce.asbeza.types.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequestDTO {

    @NotNull(message = "Address cannot be null.")
    private String address;

    @NotNull(message = "Contact number cannot be null.")
    private String phoneNumber;

}
