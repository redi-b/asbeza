package com.ecommerce.asbeza.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartRequestDTO {

    @NotNull(message = "User ID cannot be null.")
    private Long userId;

    @NotNull(message = "Items cannot be null.")
    private List<CartItemRequestDTO> items;

    @Positive(message = "Total price must be a positive value.")
    private double totalPrice;
}
