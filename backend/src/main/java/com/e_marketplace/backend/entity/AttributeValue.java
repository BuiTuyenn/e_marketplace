package com.e_marketplace.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * Entity cho bảng attribute_values
 */
@Entity
@Table(name = "attribute_values")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeValue extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private ProductAttribute attribute;
    
    @Column(name = "value", nullable = false)
    private String value;
    
    @Column(name = "color_code", length = 7)
    private String colorCode; // Mã màu hex cho thuộc tính màu sắc
    
    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;
    
    // Relationships
    @OneToMany(mappedBy = "attributeValue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<VariantAttribute> variantAttributes;
}
