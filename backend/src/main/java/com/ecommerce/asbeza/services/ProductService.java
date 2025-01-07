package com.ecommerce.asbeza.services;

import com.ecommerce.asbeza.dtos.ProductRequestDTO;
import com.ecommerce.asbeza.dtos.ProductResponseDTO;
import com.ecommerce.asbeza.exceptions.ResourceNotFoundException;
import com.ecommerce.asbeza.models.Product;
import com.ecommerce.asbeza.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    // Add new product
    public ProductResponseDTO addProduct(ProductRequestDTO requestDTO, MultipartFile imageFile) {
        Product product = modelMapper.map(requestDTO, Product.class);

        // Handle image file or image URL
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                product.setImageData(imageFile.getBytes()); // Set raw image data
            } catch (Exception e) {
                throw new RuntimeException("Failed to process image file", e);
            }
        } else if (requestDTO.getImageUrl() != null && !requestDTO.getImageUrl().isEmpty()) {
            product.setImageUrl(requestDTO.getImageUrl()); // Set image URL
        }

        // Calculate discounted price
        BigDecimal discount = requestDTO.getDiscountPercentage();
        BigDecimal discountedPrice = product.getPrice()
                .subtract(product.getPrice().multiply(discount).divide(BigDecimal.valueOf(100)));

        product.setDiscountedPrice(discountedPrice);

        // Save product
        Product savedProduct = productRepository.save(product);

        ProductResponseDTO responseDTO = modelMapper.map(savedProduct, ProductResponseDTO.class);

        // Encode image to Base64 if raw image data is present
        if (savedProduct.getImageData() != null) {
            String base64Image = Base64.getEncoder().encodeToString(savedProduct.getImageData());
            responseDTO.setImage(base64Image);
        } else if (savedProduct.getImageUrl() != null) {
            responseDTO.setImage(savedProduct.getImageUrl()); // Set image URL in the response
        }

        return responseDTO;
    }



    // Get product by ID
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        ProductResponseDTO responseDTO = modelMapper.map(product, ProductResponseDTO.class);

        // Set image URL or encode image data to Base64
        if (product.getImageData() != null) {
            String base64Image = Base64.getEncoder().encodeToString(product.getImageData());
            responseDTO.setImage(base64Image);
        } else if (product.getImageUrl() != null) {
            responseDTO.setImage(product.getImageUrl());
        }

        return responseDTO;
    }



    // Get all products
    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .sorted(Comparator.comparing(Product::getUpdatedAt).reversed())
                .map(product -> {
                    ProductResponseDTO responseDTO = modelMapper.map(product, ProductResponseDTO.class);

                    // Set image URL or encode image data to Base64
                    if (product.getImageData() != null) {
                        String base64Image = Base64.getEncoder().encodeToString(product.getImageData());
                        responseDTO.setImage(base64Image);
                    } else if (product.getImageUrl() != null) {
                        responseDTO.setImage(product.getImageUrl());
                    }

                    return responseDTO;
                })
                .collect(Collectors.toList());
    }



    // Update product by ID
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO, MultipartFile imageFile) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        // Update product details
        product.setName(requestDTO.getName());
        product.setCategory(requestDTO.getCategory());
        product.setPrice(requestDTO.getPrice());
        product.setPricePer(requestDTO.getPricePer());
        product.setStockQuantity(requestDTO.getStockQuantity());
        product.setDiscountPercentage(requestDTO.getDiscountPercentage());

        // Recalculate discounted price
        BigDecimal discount = requestDTO.getDiscountPercentage();
        BigDecimal discountedPrice = product.getPrice()
                .subtract(product.getPrice().multiply(discount).divide(BigDecimal.valueOf(100)));
        product.setDiscountedPrice(discountedPrice);

        // Update image
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                product.setImageData(imageFile.getBytes());
                product.setImageUrl(null); // Clear the URL if new raw image data is provided
            } catch (Exception e) {
                throw new RuntimeException("Failed to process image file", e);
            }
        } else if (requestDTO.getImageUrl() != null && !requestDTO.getImageUrl().isEmpty()) {
            product.setImageUrl(requestDTO.getImageUrl());
            product.setImageData(null); // Clear the image data if a URL is provided
        }

        // Save updated product
        Product updatedProduct = productRepository.save(product);

        ProductResponseDTO responseDTO = modelMapper.map(updatedProduct, ProductResponseDTO.class);

        // Set image URL or encode image data to Base64
        if (updatedProduct.getImageData() != null) {
            String base64Image = Base64.getEncoder().encodeToString(updatedProduct.getImageData());
            responseDTO.setImage(base64Image);
        } else if (updatedProduct.getImageUrl() != null) {
            responseDTO.setImage(updatedProduct.getImageUrl());
        }

        return responseDTO;
    }



    // Delete product by ID
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
    }
}
