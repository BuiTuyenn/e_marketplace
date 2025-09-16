package com.e_marketplace.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * Entity cho bảng product_attributes
 */
@Entity
@Table(name = "product_attributes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAttribute extends BaseEntity {
    
    @Column(name = "name", nullable = false, length = 100)
    private String name; // Màu sắc, Kích thước, Chất liệu
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @Builder.Default
    private AttributeType type = AttributeType.TEXT;
    
    @Column(name = "is_required", nullable = false)
    @Builder.Default
    private Boolean isRequired = false;
    
    @Column(name = "is_filterable", nullable = false)
    @Builder.Default
    private Boolean isFilterable = true;
    
    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;
    
    // Relationships
    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AttributeValue> attributeValues;
    
    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<VariantAttribute> variantAttributes;
    
    // Enum cho loại thuộc tính
    public enum AttributeType {
        TEXT,
        NUMBER,
        SELECT,
        MULTISELECT,
        BOOLEAN
    }
}
