package com.ecommerce.asbeza.responses;

import com.ecommerce.asbeza.dtos.UserDTO;
import lombok.Data;

@Data
public class AuthResponse {
    private String token;

    private UserDTO user;
}
