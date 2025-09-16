package com.e_marketplace.backend.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO cho Product entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String shortDescription;
    private String sku;
    private String status;
    private Boolean isFeatured;
    
    // Brand info
    private BrandDTO brand;
    
    // Pricing info (from variants)
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean hasDiscount;
    
    // Stock info
    private Integer totalStock;
    private Boolean inStock;
    
    // Images
    private List<ProductImageDTO> images;
    private String primaryImageUrl;
    
    // Categories
    private List<CategoryDTO> categories;
    
    // Reviews
    private Double averageRating;
    private Integer reviewCount;
    
    // Variants
    private List<ProductVariantDTO> variants;
    private ProductVariantDTO defaultVariant;
    
    // SEO
    private String seoTitle;
    private String seoDescription;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandDTO {
        private Long id;
        private String name;
        private String slug;
        private String logoUrl;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDTO {
        private Long id;
        private String name;
        private String slug;
        private Boolean isPrimary;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImageDTO {
        private Long id;
        private String imageUrl;
        private String altText;
        private Boolean isPrimary;
        private Integer sortOrder;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariantDTO {
        private Long id;
        private String name;
        private String sku;
        private BigDecimal price;
        private BigDecimal comparePrice;
        private Integer stockQuantity;
        private Boolean isDefault;
        private Boolean isActive;
        private List<VariantAttributeDTO> attributes;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantAttributeDTO {
        private String attributeName;
        private String attributeValue;
        private String colorCode;
    }
}
