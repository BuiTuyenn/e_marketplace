package com.e_marketplace.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity cho bảng product_reviews
 */
@Entity
@Table(name = "product_reviews",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "order_item_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReview extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem; // Chỉ cho phép review sản phẩm đã mua
    
    @Column(name = "rating", nullable = false)
    private Integer rating; // 1-5 stars
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    // Hình ảnh đánh giá
    @Column(name = "images", columnDefinition = "JSON")
    private String images; // Mảng URLs hình ảnh
    
    // Trạng thái
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.PENDING;
    
    @Column(name = "is_verified_purchase", nullable = false)
    @Builder.Default
    private Boolean isVerifiedPurchase = false;
    
    // Phản hồi từ shop
    @Column(name = "admin_reply", columnDefinition = "TEXT")
    private String adminReply;
    
    @Column(name = "admin_replied_at")
    private LocalDateTime adminRepliedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_replied_by")
    private User adminRepliedBy;
    
    // Enums
    public enum ReviewStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
    
    // Helper methods
    public boolean isApproved() {
        return status == ReviewStatus.APPROVED;
    }
    
    public boolean hasAdminReply() {
        return adminReply != null && !adminReply.trim().isEmpty();
    }
    
    public boolean isValidRating() {
        return rating >= 1 && rating <= 5;
    }
}
