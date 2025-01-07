package com.ecommerce.asbeza.repositories;

import com.ecommerce.asbeza.models.User;
import com.ecommerce.asbeza.types.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

//    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findByRole(Role role);
}
