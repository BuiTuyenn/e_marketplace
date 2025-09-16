package com.e_marketplace.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Entity cho bảng products
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;
    
    @Column(name = "sku", unique = true, length = 100)
    private String sku;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;
    
    @Column(name = "weight", precision = 8, scale = 2)
    private BigDecimal weight; // Trọng lượng (gram)
    
    @Column(name = "dimensions", length = 100)
    private String dimensions; // Kích thước (DxRxC cm)
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.DRAFT;
    
    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;
    
    @Column(name = "seo_title")
    private String seoTitle;
    
    @Column(name = "seo_description", columnDefinition = "TEXT")
    private String seoDescription;
    
    // Relationships
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProductCategory> productCategories;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProductVariant> variants;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProductImage> images;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProductReview> reviews;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Wishlist> wishlists;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProductComparison> comparisons;
    
    // Enum cho trạng thái sản phẩm
    public enum ProductStatus {
        DRAFT,
        ACTIVE,
        INACTIVE,
        OUT_OF_STOCK
    }
    
    // Helper methods
    public ProductVariant getDefaultVariant() {
        return variants.stream()
                .filter(variant -> Boolean.TRUE.equals(variant.getIsDefault()))
                .findFirst()
                .orElse(null);
    }
    
    public BigDecimal getMinPrice() {
        return variants.stream()
                .map(variant -> variant.getPrice())
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
    
    public BigDecimal getMaxPrice() {
        return variants.stream()
                .map(variant -> variant.getPrice())
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
    
    public int getTotalStock() {
        return variants.stream()
                .mapToInt(variant -> variant.getStockQuantity())
                .sum();
    }
    
    public boolean isInStock() {
        return getTotalStock() > 0;
    }
    
    public ProductImage getPrimaryImage() {
        return images.stream()
                .filter(image -> Boolean.TRUE.equals(image.getIsPrimary()))
                .findFirst()
                .orElse(null);
    }
    
    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToInt(review -> review.getRating())
                .average()
                .orElse(0.0);
    }
    
    public int getReviewCount() {
        return reviews != null ? reviews.size() : 0;
    }
}
