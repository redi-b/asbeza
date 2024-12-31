package com.ecommerce.asbeza.dtos;

import com.ecommerce.asbeza.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long userId;
    private String name;
    private String email;
    private String password;
    private Role role;
    private CartDTO cart;
}
