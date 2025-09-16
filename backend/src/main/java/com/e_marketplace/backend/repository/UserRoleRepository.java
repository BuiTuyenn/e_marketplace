package com.e_marketplace.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_marketplace.backend.entity.User;
import com.e_marketplace.backend.entity.UserRole;

/**
 * Repository for UserRole entity
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    
    /**
     * Find user role by user
     */
    Optional<UserRole> findByUser(User user);
    
    /**
     * Find all roles for a user
     */
    List<UserRole> findAllByUser(User user);
    
    /**
     * Check if user has specific role
     */
    boolean existsByUserAndRole_Name(User user, String roleName);
    
    /**
     * Delete all roles for a user
     */
    void deleteByUser(User user);
}
