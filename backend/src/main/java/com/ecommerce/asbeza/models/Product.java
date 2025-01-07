package com.ecommerce.asbeza.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", sequenceName = "product_sequence", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Product name is required and cannot be blank.")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters.")
    @Column(nullable = false)
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters.")
    private String description;

    @Positive(message = "Price must be a positive value.")
    @Column(nullable = false)
    private BigDecimal price;

    private String pricePer;

    @Positive(message = "Quantity must be a positive value.")
    @Column(nullable = false)
    private int stockQuantity;

    private BigDecimal discountPercentage = BigDecimal.ZERO;

    private BigDecimal discountedPrice;

    @NotBlank(message = "Category is required and cannot be blank.")
    @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters.")
    @Column(nullable = false)
    private String category;
    
    @Column(name = "image_data", columnDefinition = "BYTEA")
    private byte[] imageData; // Field for storing image data as a BLOB

    @Size(max = 500, message = "Image URL must not exceed 500 characters.")
    @Column(name = "image_url")
    private String imageUrl; // Field for storing image URL

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public boolean hasImageUrl() {
        return this.imageUrl != null && !this.imageUrl.isEmpty();
    }

    public boolean hasImageData() {
        return this.imageData != null && this.imageData.length > 0;
    }
}
