package com.e_marketplace.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity cho bảng notifications
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "type", length = 50)
    @Builder.Default
    private String type = "info"; // info, success, warning, error
    
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;
    
    @Column(name = "data", columnDefinition = "JSON")
    private String data; // Dữ liệu bổ sung
    
    // Helper methods
    public void markAsRead() {
        this.isRead = true;
    }
    
    public boolean isUnread() {
        return !isRead;
    }
}
