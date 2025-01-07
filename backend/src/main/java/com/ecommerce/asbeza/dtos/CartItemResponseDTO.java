package com.ecommerce.asbeza.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponseDTO {

    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal productPrice;
    private String image;
}
