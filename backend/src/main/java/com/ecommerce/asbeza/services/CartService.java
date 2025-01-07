package com.ecommerce.asbeza.services;

import com.ecommerce.asbeza.dtos.*;
import com.ecommerce.asbeza.models.*;
import com.ecommerce.asbeza.repositories.*;
import com.ecommerce.asbeza.types.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public List<CartResponseDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        return carts.stream()
                .map(cart -> modelMapper.map(cart, CartResponseDTO.class))
                .collect(Collectors.toList());
    }

    // Get current user cart
    public CartResponseDTO getUserCart() {
        return getCartByUserId(getAuthenticatedUser().getId());
    }

    // Get Cart by User
    public CartResponseDTO getCartByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Check if the authenticated user is allowed to access this cart
        User authenticatedUser = getAuthenticatedUser();
        if (!authenticatedUser.getId().equals(userId) && !authenticatedUser.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("Access denied to view this cart.");
        }

        if (!cartRepository.existsByUser(user)) {
            throw new IllegalStateException("This user does not have a cart");
        }

        Cart cart = user.getCart();
        CartResponseDTO response = modelMapper.map(cart, CartResponseDTO.class);
        for (int i = 0; i < cart.getItems().size(); i++) {
            String image = getImage(cart.getItems().get(i).getProduct());
            response.getItems().get(i).setImage(image);
        }

        return response;
    }

    public CartResponseDTO updateCart(CartRequestDTO cartRequestDTO) {
        User user = getAuthenticatedUser();

        if (!cartRepository.existsByUser(user)) {
            throw new IllegalStateException("This user does not have a cart.");
        }

        // Get or initialize the user's cart
        Cart cart = user.getCart();

        // Clear existing items in the cart
        cart.getItems().clear();

        BigDecimal totalPrice = BigDecimal.ZERO; // Initialize total price

        // Process each item in the request
        for (CartItemRequestDTO item : cartRequestDTO.getItems()) {
            // Find the product by ID
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getProductId()));

            // Create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart); // Explicitly set the cart
            cartItem.setProduct(product);
            cartItem.setQuantity(item.getQuantity());

            // Calculate subtotal for this item and add to total price
            totalPrice = totalPrice.add(cartItem.getSubtotal());

            // Add the item to the cart
            cart.getItems().add(cartItem);
        }

        // Set the total price in the cart
        cart.setTotalPrice(totalPrice.doubleValue());

        // Save and return the updated cart
        cart = cartRepository.save(cart);
        return modelMapper.map(cart, CartResponseDTO.class);
    }


    public CartResponseDTO addCartItem(Long productId, int quantity) {
        User user = getAuthenticatedUser();

        if (!cartRepository.existsByUser(user)) {
            throw new IllegalStateException("This user does not have a cart.");
        }
        Cart cart = user.getCart();

        // Find the product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        // Check if item already exists in the cart
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Update quantity if item already exists
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Add new item
            CartItem newItem = new CartItem(null, cart, product, quantity, LocalDateTime.now());
            cart.getItems().add(newItem);
        }

        // Recalculate total price
        recalculateCartTotal(cart);

        cart = cartRepository.save(cart);
        return modelMapper.map(cart, CartResponseDTO.class);
    }

    public CartResponseDTO removeCartItem(Long productId) {
        User user = getAuthenticatedUser();

        if (!cartRepository.existsByUser(user)) {
            throw new IllegalStateException("This user does not have a cart.");
        }
        Cart cart = user.getCart();

        // Remove item from the cart
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

        // Recalculate total price
        recalculateCartTotal(cart);

        cart = cartRepository.save(cart);
        return modelMapper.map(cart, CartResponseDTO.class);
    }

    public CartResponseDTO updateCartItemQuantity(Long productId, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }

        User user = getAuthenticatedUser();

        if (!cartRepository.existsByUser(user)) {
            throw new IllegalStateException("This user does not have a cart.");
        }
        Cart cart = user.getCart();

        // Find the item
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart: " + productId));

        // Update quantity
        existingItem.setQuantity(quantity);

        // Recalculate total price
        recalculateCartTotal(cart);

        cart = cartRepository.save(cart);
        return modelMapper.map(cart, CartResponseDTO.class);
    }

    // Clear Cart
    public void clearCart(Long userId) {
        User user = getAuthenticatedUser();

        if (!cartRepository.existsByUser(user)) {
            throw new IllegalStateException("This user does not have a cart.");
        }

        if (!user.getId().equals(userId) && !user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("Access denied to clear this cart.");
        }

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for user ID: " + userId));

        cart.getItems().clear();
        cart.setTotalPrice(0);
        cartRepository.save(cart);
    }

    private void recalculateCartTotal(Cart cart) {
        BigDecimal totalPrice = cart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalPrice(totalPrice.doubleValue());
    }

    // Authenticate User
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private String getImage(Product product) {
        if (product.hasImageData()) {
            return Base64.getEncoder().encodeToString(product.getImageData());
        } else if (product.hasImageUrl()) {
            return product.getImageUrl();
        }
        return null; // Return null if no image is available
    }
}
