package com.e_marketplace.backend.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO cho User entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    private Long id;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String fullName;
    private String avatarUrl;
    private String status;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    
    // Roles
    private List<String> roles;
    
    // Addresses
    private List<AddressDTO> addresses;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDTO {
        private Long id;
        private String label;
        private String recipientName;
        private String recipientPhone;
        private String addressLine1;
        private String addressLine2;
        private String ward;
        private String district;
        private String city;
        private String postalCode;
        private Boolean isDefault;
    }
}
