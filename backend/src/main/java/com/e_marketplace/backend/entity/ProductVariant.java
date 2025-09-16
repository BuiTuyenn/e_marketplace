package com.e_marketplace.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Entity cho bảng product_variants
 */
@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "name", nullable = false)
    private String name; // Tên biến thể: Áo thun đỏ size M
    
    @Column(name = "sku", unique = true, length = 100)
    private String sku;
    
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    
    @Column(name = "compare_price", precision = 12, scale = 2)
    private BigDecimal comparePrice; // Giá gốc (để hiển thị % giảm)
    
    @Column(name = "cost_price", precision = 12, scale = 2)
    private BigDecimal costPrice; // Giá nhập
    
    @Column(name = "stock_quantity", nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;
    
    @Column(name = "low_stock_threshold", nullable = false)
    @Builder.Default
    private Integer lowStockThreshold = 10;
    
    @Column(name = "weight", precision = 8, scale = 2)
    private BigDecimal weight;
    
    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    // Relationships
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<VariantAttribute> variantAttributes;
    
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProductImage> images;
    
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CartItem> cartItems;
    
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems;
    
    // Helper methods
    public boolean isInStock() {
        return stockQuantity > 0;
    }
    
    public boolean isLowStock() {
        return stockQuantity <= lowStockThreshold;
    }
    
    public BigDecimal getDiscountAmount() {
        if (comparePrice != null && comparePrice.compareTo(price) > 0) {
            return comparePrice.subtract(price);
        }
        return BigDecimal.ZERO;
    }
    
    public double getDiscountPercentage() {
        if (comparePrice != null && comparePrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = getDiscountAmount();
            return discount.divide(comparePrice, 4, java.math.RoundingMode.HALF_UP)
                          .multiply(BigDecimal.valueOf(100))
                          .doubleValue();
        }
        return 0.0;
    }
    
    public boolean hasDiscount() {
        return getDiscountAmount().compareTo(BigDecimal.ZERO) > 0;
    }
}
