package com.e_marketplace.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Entity cho bảng coupons
 */
@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends BaseEntity {
    
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    // Loại giảm giá
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CouponType type;
    
    @Column(name = "value", nullable = false, precision = 12, scale = 2)
    private BigDecimal value; // Giá trị giảm giá
    
    // Điều kiện áp dụng
    @Column(name = "minimum_amount", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal minimumAmount = BigDecimal.ZERO; // Giá trị đơn hàng tối thiểu
    
    @Column(name = "maximum_discount", precision = 12, scale = 2)
    private BigDecimal maximumDiscount; // Giảm tối đa (cho % discount)
    
    // Giới hạn sử dụng
    @Column(name = "usage_limit")
    private Integer usageLimit; // Số lần sử dụng tối đa
    
    @Column(name = "usage_limit_per_user")
    @Builder.Default
    private Integer usageLimitPerUser = 1;
    
    @Column(name = "used_count")
    @Builder.Default
    private Integer usedCount = 0;
    
    // Thời gian hiệu lực
    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    // Relationships
    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CouponUsage> couponUsages;
    
    // Enums
    public enum CouponType {
        PERCENTAGE,
        FIXED_AMOUNT,
        FREE_SHIPPING
    }
    
    // Helper methods
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && 
               startsAt.isBefore(now) && 
               expiresAt.isAfter(now) &&
               (usageLimit == null || usedCount < usageLimit);
    }
    
    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (!isValid() || orderAmount.compareTo(minimumAmount) < 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discount = BigDecimal.ZERO;
        
        switch (type) {
            case PERCENTAGE -> {
                discount = orderAmount.multiply(value).divide(BigDecimal.valueOf(100));
                if (maximumDiscount != null && discount.compareTo(maximumDiscount) > 0) {
                    discount = maximumDiscount;
                }
            }
            case FIXED_AMOUNT -> discount = value;
            case FREE_SHIPPING -> {
                // Logic for free shipping will be handled in service layer
                discount = BigDecimal.ZERO;
            }
        }
        
        return discount;
    }
    
    public boolean canBeUsed() {
        return usageLimit == null || usedCount < usageLimit;
    }
    
    public boolean canBeUsedByUser(User user, int userUsageCount) {
        return userUsageCount < usageLimitPerUser;
    }
}
