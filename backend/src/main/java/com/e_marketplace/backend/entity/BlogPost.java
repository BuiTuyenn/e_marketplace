package com.e_marketplace.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity cho bảng blog_posts
 */
@Entity
@Table(name = "blog_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogPost extends BaseEntity {
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;
    
    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;
    
    @Column(name = "excerpt", columnDefinition = "TEXT")
    private String excerpt;
    
    @Column(name = "featured_image", length = 500)
    private String featuredImage;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PostStatus status = PostStatus.DRAFT;
    
    // SEO
    @Column(name = "seo_title")
    private String seoTitle;
    
    @Column(name = "seo_description", columnDefinition = "TEXT")
    private String seoDescription;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    // Enums
    public enum PostStatus {
        DRAFT,
        PUBLISHED
    }
    
    // Helper methods
    public boolean isPublished() {
        return status == PostStatus.PUBLISHED;
    }
}
