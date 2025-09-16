package com.e_marketplace.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Cấu hình JPA và auditing
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.e_marketplace.backend.repository")
public class JpaConfig {
    // JPA auditing sẽ tự động set createdAt và updatedAt
}
