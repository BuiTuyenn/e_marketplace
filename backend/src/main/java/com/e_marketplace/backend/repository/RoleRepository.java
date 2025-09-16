package com.e_marketplace.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_marketplace.backend.entity.Role;

/**
 * Repository for Role entity
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Find role by name
     */
    Optional<Role> findByName(String name);
    
    /**
     * Check if role exists by name
     */
    boolean existsByName(String name);
}
