package com.ecommerce.asbeza.controllers;

import com.ecommerce.asbeza.dtos.CartRequestDTO;
import com.ecommerce.asbeza.dtos.CartResponseDTO;
import com.ecommerce.asbeza.services.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 1. Get User Cart
    @GetMapping("/get-cart")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartResponseDTO> getCart() {
        CartResponseDTO cart = cartService.getUserCart();
        return ResponseEntity.ok(cart);
    }

    // 2. Update Entire Cart
    @PutMapping("/update-cart")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartResponseDTO> updateCart(@Valid @RequestBody CartRequestDTO cartRequestDTO) {
        CartResponseDTO updatedCart = cartService.updateCart(cartRequestDTO);
        return ResponseEntity.ok(updatedCart);
    }

    // 3. Add Single Item to Cart
    @PostMapping("/add-item")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartResponseDTO> addCartItem(
            @RequestParam Long productId,
            @RequestParam int quantity) {
        CartResponseDTO updatedCart = cartService.addCartItem(productId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    // 4. Remove Single Item from Cart
    @DeleteMapping("/remove-item")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartResponseDTO> removeCartItem(@RequestParam Long productId) {
        CartResponseDTO updatedCart = cartService.removeCartItem(productId);
        return ResponseEntity.ok(updatedCart);
    }

    // 5. Update Quantity of Single Item
    @PutMapping("/update-quantity")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartResponseDTO> updateCartItemQuantity(
            @RequestParam Long productId,
            @RequestParam int quantity) {
        CartResponseDTO updatedCart = cartService.updateCartItemQuantity(productId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    // 6. Clear Cart
    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    // 7. Get cart by userID
    @GetMapping("/get-cart/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CartResponseDTO> getCartByUserId(@Valid @PathVariable Long userId) {
        CartResponseDTO cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    // 8. Get all carts
    @GetMapping("/get-carts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllCarts() {
        Map<String, Object> response = new HashMap<>();
        List<CartResponseDTO> products = cartService.getAllCarts();
        response.put("message", "Carts fetched successfully.");
        response.put("data", products);
        response.put("status", HttpStatus.OK.value());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
