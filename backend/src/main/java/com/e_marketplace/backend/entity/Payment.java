package com.e_marketplace.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity cho bảng payments
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;
    
    // Thông tin giao dịch
    @Column(name = "transaction_id")
    private String transactionId; // ID từ cổng thanh toán
    
    @Column(name = "gateway_response", columnDefinition = "JSON")
    private String gatewayResponse; // Response từ payment gateway
    
    // Timestamps
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    
    @Column(name = "failed_at")
    private LocalDateTime failedAt;
    
    // Enums
    public enum PaymentMethod {
        COD,
        BANK_TRANSFER,
        CREDIT_CARD,
        MOMO,
        ZALOPAY,
        SHOPEEPAY
    }
    
    public enum PaymentStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELLED,
        REFUNDED
    }
    
    // Helper methods
    public boolean isCompleted() {
        return status == PaymentStatus.COMPLETED;
    }
    
    public boolean isFailed() {
        return status == PaymentStatus.FAILED;
    }
    
    public boolean isPending() {
        return status == PaymentStatus.PENDING;
    }
}
