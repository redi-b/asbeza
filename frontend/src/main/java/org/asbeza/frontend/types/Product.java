package org.asbeza.frontend.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String pricePer;
    private int stockQuantity;
    private BigDecimal discountPercentage;
    private BigDecimal discountedPrice;
    private String category;
    private String image;

}
