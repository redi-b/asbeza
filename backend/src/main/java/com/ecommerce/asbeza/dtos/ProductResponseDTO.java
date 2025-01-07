package com.ecommerce.asbeza.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private String pricePer;
    private int stockQuantity;
    private BigDecimal discountPercentage;
    private BigDecimal discountedPrice;
    private String description;
    private String image;
}
