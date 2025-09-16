package com.e_marketplace.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity cho bảng user_addresses
 */
@Entity
@Table(name = "user_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddress extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "label", length = 50)
    private String label; // Nhà riêng, Văn phòng, etc.
    
    @Column(name = "recipient_name", nullable = false, length = 100)
    private String recipientName;
    
    @Column(name = "recipient_phone", nullable = false, length = 20)
    private String recipientPhone;
    
    @Column(name = "address_line_1", nullable = false)
    private String addressLine1;
    
    @Column(name = "address_line_2")
    private String addressLine2;
    
    @Column(name = "ward", length = 100)
    private String ward;
    
    @Column(name = "district", length = 100)
    private String district;
    
    @Column(name = "city", nullable = false, length = 100)
    private String city;
    
    @Column(name = "postal_code", length = 20)
    private String postalCode;
    
    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;
    
    // Helper method để lấy địa chỉ đầy đủ
    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();
        fullAddress.append(addressLine1);
        
        if (addressLine2 != null && !addressLine2.trim().isEmpty()) {
            fullAddress.append(", ").append(addressLine2);
        }
        if (ward != null && !ward.trim().isEmpty()) {
            fullAddress.append(", ").append(ward);
        }
        if (district != null && !district.trim().isEmpty()) {
            fullAddress.append(", ").append(district);
        }
        fullAddress.append(", ").append(city);
        
        if (postalCode != null && !postalCode.trim().isEmpty()) {
            fullAddress.append(" ").append(postalCode);
        }
        
        return fullAddress.toString();
    }
}
