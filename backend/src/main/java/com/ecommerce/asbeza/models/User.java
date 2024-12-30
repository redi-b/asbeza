package com.ecommerce.asbeza.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @Size(min = 3, max = 20, message = "Name should be 3 to 20 characters long")
    @Pattern(regexp = "^[a-zA-Z]*$", message = "Name should only contain valid alphabetical characters")
    private String name;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @Size(min = 8, message = "Password should at least be 8 characters long")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToOne(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
    private Cart cart;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}