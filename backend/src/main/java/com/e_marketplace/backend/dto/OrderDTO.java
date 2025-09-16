package com.e_marketplace.backend.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO cho Order entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    
    private Long id;
    private String orderNumber;
    private String status;
    private String paymentStatus;
    private String paymentMethod;
    
    // Customer info
    private CustomerDTO customer;
    
    // Address
    private AddressDTO shippingAddress;
    
    // Order totals
    private BigDecimal subtotal;
    private BigDecimal shippingFee;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    
    // Order items
    private List<OrderItemDTO> items;
    
    // Notes
    private String notes;
    private String adminNotes;
    
    // Tracking
    private String trackingNumber;
    private String shippingProvider;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerDTO {
        private Long id;
        private String email;
        private String phone;
        private String name;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDTO {
        private String recipientName;
        private String recipientPhone;
        private String addressLine1;
        private String addressLine2;
        private String ward;
        private String district;
        private String city;
        private String postalCode;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private Long id;
        private String productName;
        private String variantName;
        private String sku;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal totalPrice;
    }
}
