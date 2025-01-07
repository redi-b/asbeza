package com.ecommerce.asbeza.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {

    @NotBlank(message = "Product name is required.")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters.")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters.")
    private String description;

    @NotBlank(message = "Category is required.")
    @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters.")
    private String category;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0.")
    private BigDecimal price;

    private String pricePer;

    @Min(value = 0, message = "Stock quantity cannot be negative.")
    private int stockQuantity;

    @DecimalMin(value = "0.0", message = "Discount cannot be negative.")
    @DecimalMax(value = "100.0", message = "Discount cannot exceed 100%.")
    private BigDecimal discountPercentage;

    @Size(max = 500, message = "Image URL must not exceed 500 characters.")
    private String imageUrl;
}
