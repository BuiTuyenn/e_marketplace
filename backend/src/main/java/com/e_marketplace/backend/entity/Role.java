package com.e_marketplace.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * Entity cho bảng roles
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {
    
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserRole> userRoles;
    
    // Enum cho các vai trò chuẩn
    public enum RoleName {
        ADMIN("admin"),
        CUSTOMER("customer"), 
        VENDOR("vendor");
        
        private final String value;
        
        RoleName(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
}
