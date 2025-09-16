package com.e_marketplace.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity cho bảng pages (CMS)
 */
@Entity
@Table(name = "pages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Page extends BaseEntity {
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;
    
    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;
    
    @Column(name = "excerpt", columnDefinition = "TEXT")
    private String excerpt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PageStatus status = PageStatus.DRAFT;
    
    // SEO
    @Column(name = "seo_title")
    private String seoTitle;
    
    @Column(name = "seo_description", columnDefinition = "TEXT")
    private String seoDescription;
    
    // Enums
    public enum PageStatus {
        DRAFT,
        PUBLISHED
    }
    
    // Helper methods
    public boolean isPublished() {
        return status == PageStatus.PUBLISHED;
    }
}
