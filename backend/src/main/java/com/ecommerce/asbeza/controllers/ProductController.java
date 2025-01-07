package com.ecommerce.asbeza.controllers;

import com.ecommerce.asbeza.dtos.ProductRequestDTO;
import com.ecommerce.asbeza.dtos.ProductResponseDTO;
import com.ecommerce.asbeza.services.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin
public class ProductController {

    private final ProductService productService;

    @PostMapping(value = "/admin/products/add-product", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createProduct(
            @RequestParam("product") String productJson, // Accept as String
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        // Parse the JSON manually
        ObjectMapper objectMapper = new ObjectMapper();
        ProductRequestDTO productRequestDTO;
        try {
            productRequestDTO = objectMapper.readValue(productJson, ProductRequestDTO.class);
        } catch (JsonProcessingException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid JSON format");
            error.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        // Process the product
        Map<String, Object> response = new HashMap<>();
        try {
            ProductResponseDTO product = productService.addProduct(productRequestDTO, imageFile);
            response.put("message", "Product created successfully.");
            response.put("data", product);
            response.put("status", HttpStatus.CREATED.value());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping("/products/get-products")
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        Map<String, Object> response = new HashMap<>();
        List<ProductResponseDTO> products = productService.getAllProducts();
        response.put("message", "Products fetched successfully.");
        response.put("data", products);
        response.put("status", HttpStatus.OK.value());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            ProductResponseDTO product = productService.getProductById(id);
            response.put("message", "Product fetched successfully.");
            response.put("data", product);
            response.put("status", HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable Long id,
            @RequestParam("product") String productJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        // Parse the JSON manually
        ObjectMapper objectMapper = new ObjectMapper();
        ProductRequestDTO productRequestDTO;
        try {
            productRequestDTO = objectMapper.readValue(productJson, ProductRequestDTO.class);
        } catch (JsonProcessingException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid JSON format");
            error.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> response = new HashMap<>();
        try {
            ProductResponseDTO updatedProduct = productService.updateProduct(id, productRequestDTO, imageFile);
            response.put("message", "Product updated successfully.");
            response.put("data", updatedProduct);
            response.put("status", HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            productService.deleteProduct(id);
            response.put("message", "Product deleted successfully.");
            response.put("status", HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
