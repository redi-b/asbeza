package com.ecommerce.asbeza.services;

import com.ecommerce.asbeza.dtos.*;
import com.ecommerce.asbeza.models.Cart;
import com.ecommerce.asbeza.models.User;
import com.ecommerce.asbeza.repositories.UserRepository;
import com.ecommerce.asbeza.types.Role;
import com.ecommerce.asbeza.utils.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public String registerUser(UserRequestDTO userUpdateDTO, Role userRole) {
        if (userRepository.existsByEmail(userUpdateDTO.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        User user = modelMapper.map(userUpdateDTO, User.class);
        user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        user.setRole(userRole);
        if (userRole == Role.CUSTOMER) {
            Cart cart = new Cart();
            cart.setUser(user);
            user.setCart(cart);
        }

        user = userRepository.save(user);
        return loginUser(
                new LoginRequestDTO(user.getEmail(), userUpdateDTO.getPassword()), userRole
        );
    }

    public String loginUser(LoginRequestDTO loginRequestDTO, Role requiredRole) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getEmail(),
                        loginRequestDTO.getPassword()
                )
        );

        // Extract user details
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        // Verify role before generating token
        if (!user.getRole().equals(requiredRole)) {
            throw new IllegalArgumentException("Access denied for this role.");
        }

        // Generate token with roles from Authentication object
        return jwtUtil.generateToken(authentication, user.getId(), user.getName());
    }

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .sorted(Comparator.comparing(User::getUpdatedAt).reversed())
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        // Find user by ID
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        // Update fields if provided
        if (userUpdateDTO.getName() != null && !userUpdateDTO.getName().isEmpty()) {
            user.setName(userUpdateDTO.getName());
        }
        if (userUpdateDTO.getEmail() != null && !userUpdateDTO.getEmail().isEmpty()) {
            // Ensure email is unique
            if (!user.getEmail().equals(userUpdateDTO.getEmail()) &&
                    userRepository.existsByEmail(userUpdateDTO.getEmail())) {
                throw new IllegalArgumentException("Email is already registered.");
            }
            user.setEmail(userUpdateDTO.getEmail());
        }
        if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }
        if (userUpdateDTO.getRole() != null) {
            user.setRole(userUpdateDTO.getRole());
        }

        // Save updated user
        User updatedUser = userRepository.save(user);

        // Return updated user details as DTO
        return modelMapper.map(updatedUser, UserResponseDTO.class);
    }


}
