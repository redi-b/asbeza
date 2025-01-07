package com.ecommerce.asbeza.controllers;

import com.ecommerce.asbeza.dtos.*;
import com.ecommerce.asbeza.services.OrderService;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Place an Order
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@Valid @RequestBody OrderRequestDTO orderRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            OrderResponseDTO order = orderService.createOrder(orderRequest);
            response.put("message", "Order created successfully.");
            response.put("data", order);
            response.put("status", HttpStatus.CREATED.value());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Get Order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Long orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            OrderResponseDTO order = orderService.getOrderById(orderId);
            response.put("message", "Order fetched successfully.");
            response.put("data", order);
            response.put("status", HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // Get All Orders for Logged-in User
    @GetMapping
    public ResponseEntity<Map<String, Object>> getOrdersForUser() {
        Map<String, Object> response = new HashMap<>();
        List<OrderResponseDTO> orders = orderService.getOrdersForUser();
        response.put("message", "Orders fetched successfully.");
        response.put("data", orders);
        response.put("status", HttpStatus.OK.value());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Update Order Status (Admin or Delivery Personnel Only)
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderUpdateDTO orderUpdateDTO
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            OrderResponseDTO order = orderService.updateOrderStatus(orderId, orderUpdateDTO);
            response.put("message", "Order status updated successfully.");
            response.put("data", order);
            response.put("status", HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // Cancel Order (Customer Only)
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable Long orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            orderService.cancelOrder(orderId);
            response.put("message", "Order cancelled successfully.");
            response.put("status", HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // Get all orders
    @GetMapping("/get-orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllCarts() {
        Map<String, Object> response = new HashMap<>();
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        response.put("message", "Carts fetched successfully.");
        response.put("data", orders);
        response.put("status", HttpStatus.OK.value());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Update delivery personnel for an order
    @PatchMapping("/update-delivery-personnel/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateDeliveryPersonnel(
            @PathVariable Long orderId,
            @RequestBody DeliveryPersonnelUpdateDTO deliveryPersonnelUpdateDTO) {
        Map<String, Object> response = new HashMap<>();
        OrderResponseDTO updatedOrder = orderService.updateDeliveryPersonnel(orderId, deliveryPersonnelUpdateDTO.getDeliveryPersonnelId());
        response.put("message", "Delivery personnel updated successfully.");
        response.put("data", updatedOrder);
        response.put("status", HttpStatus.OK.value());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Get all orders for a delivery personnel
    @GetMapping("/get-orders-by-delivery-personnel/{deliveryPersonnelId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DELIVERY_PERSONNEL')")
    public ResponseEntity<Map<String, Object>> getOrdersByDeliveryPersonnel(@PathVariable Long deliveryPersonnelId) {
        Map<String, Object> response = new HashMap<>();
        List<OrderResponseDTO> orders = orderService.getOrdersForDeliveryPersonnel(deliveryPersonnelId);
        response.put("message", "Orders fetched successfully for delivery personnel.");
        response.put("data", orders);
        response.put("status", HttpStatus.OK.value());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}