package com.e_marketplace.backend.repository;

import com.e_marketplace.backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho Product entity
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findBySlug(String slug);
    
    Optional<Product> findBySku(String sku);
    
    List<Product> findByStatus(Product.ProductStatus status);
    
    List<Product> findByIsFeaturedTrue();
    
    @Query("SELECT p FROM Product p WHERE p.status = :status")
    Page<Product> findByStatus(@Param("status") Product.ProductStatus status, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isFeatured = true AND p.status = 'ACTIVE'")
    List<Product> findFeaturedProducts();
    
    @Query("SELECT p FROM Product p JOIN p.productCategories pc WHERE pc.category.id = :categoryId AND p.status = 'ACTIVE'")
    Page<Product> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.brand.id = :brandId AND p.status = 'ACTIVE'")
    Page<Product> findByBrandId(@Param("brandId") Long brandId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "p.status = 'ACTIVE'")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.status = :status")
    long countByStatus(@Param("status") Product.ProductStatus status);
}
