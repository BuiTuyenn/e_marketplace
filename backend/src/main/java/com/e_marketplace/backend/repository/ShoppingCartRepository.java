package com.e_marketplace.backend.repository;

import com.e_marketplace.backend.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository cho ShoppingCart entity
 */
@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    
    Optional<ShoppingCart> findByUserId(Long userId);
    
    Optional<ShoppingCart> findBySessionId(String sessionId);
    
    void deleteByUserId(Long userId);
    
    void deleteBySessionId(String sessionId);
}
