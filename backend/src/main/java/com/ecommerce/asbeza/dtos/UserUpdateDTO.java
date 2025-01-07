package com.ecommerce.asbeza.dtos;

import com.ecommerce.asbeza.types.Role;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    private String name;

    @Email
    private String email;

    private Role role;

    private String password;
}
