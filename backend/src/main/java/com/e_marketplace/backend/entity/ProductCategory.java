package com.e_marketplace.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity cho bảng product_categories (Many-to-Many relationship)
 */
@Entity
@Table(name = "product_categories",
       uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "category_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategory extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private Boolean isPrimary = false; // Đánh dấu danh mục chính
}
