package com.ecommerce.asbeza.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDTO {

    private Long id;
    private Long userId;
    private List<CartItemResponseDTO> items;
    private double totalPrice;
}
