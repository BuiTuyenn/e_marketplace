package com.e_marketplace.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity cho bảng settings
 */
@Entity
@Table(name = "settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Setting extends BaseEntity {
    
    @Column(name = "key_name", nullable = false, unique = true, length = 100)
    private String keyName;
    
    @Column(name = "value", columnDefinition = "TEXT")
    private String value;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    // Helper methods để type casting
    public Integer getIntValue() {
        try {
            return value != null ? Integer.valueOf(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    public Double getDoubleValue() {
        try {
            return value != null ? Double.valueOf(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    public Boolean getBooleanValue() {
        return value != null ? Boolean.valueOf(value) : null;
    }
}
