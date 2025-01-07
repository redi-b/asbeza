package com.ecommerce.asbeza.controllers;

import com.ecommerce.asbeza.dtos.LoginRequestDTO;
import com.ecommerce.asbeza.dtos.UserRequestDTO;
import com.ecommerce.asbeza.dtos.UserResponseDTO;
import com.ecommerce.asbeza.dtos.UserUpdateDTO;
import com.ecommerce.asbeza.models.User;
import com.ecommerce.asbeza.services.UserService;
import com.ecommerce.asbeza.types.Role;
import jakarta.persistence.EntityNotFoundException;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    // Endpoint for customer registration
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerCustomer(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            String result = userService.registerUser(userRequestDTO, Role.CUSTOMER);
            response.put("message", "User registered successfully.");
            response.put("data", result);
            response.put("status", HttpStatus.CREATED.value());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Login endpoint for customers
    @PostMapping("/login/customer")
    public ResponseEntity<Map<String, Object>> loginCustomer(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return processLogin(loginRequestDTO, Role.CUSTOMER);
    }

    // Login endpoint for admins
    @PostMapping("/login/admin")
    public ResponseEntity<Map<String, Object>> loginAdmin(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return processLogin(loginRequestDTO, Role.ADMIN);
    }

    // Login endpoint for delivery personnel
    @PostMapping("/login/delivery")
    public ResponseEntity<Map<String, Object>> loginDelivery(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return processLogin(loginRequestDTO, Role.DELIVERY_PERSONNEL);
    }

    // Get user by ID for admin
    @GetMapping("/admin/get-user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserResponseDTO user = userService.getUserById(id);
            response.put("message", "User fetched successfully.");
            response.put("data", user);
            response.put("status", HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response.put("message", "User not found.");
            response.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "An error occurred.");
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get users for admin
    @GetMapping("/admin/get-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        List<UserResponseDTO> users = userService.getAllUsers();
        response.put("message", "Users fetched successfully.");
        response.put("data", users);
        response.put("status", HttpStatus.OK.value());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Common method to process login for all roles
    private ResponseEntity<Map<String, Object>> processLogin(LoginRequestDTO loginRequestDTO, Role role) {
        Map<String, Object> response = new HashMap<>();
        try {
            String token = userService.loginUser(loginRequestDTO, role);
            response.put("message", "Login successful.");
            response.put("token", token);
            response.put("status", HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/admin/update-user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateDTO userUpdateDTO) {

        Map<String, Object> response = new HashMap<>();
        try {
            UserResponseDTO updatedUser = userService.updateUser(id, userUpdateDTO);
            response.put("message", "User updated successfully.");
            response.put("data", updatedUser);
            response.put("status", HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "An error occurred while updating the user.");
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
