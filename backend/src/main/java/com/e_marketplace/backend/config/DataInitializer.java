package com.e_marketplace.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.e_marketplace.backend.entity.Role;
import com.e_marketplace.backend.repository.RoleRepository;

/**
 * Khởi tạo dữ liệu để tạo các vai trò mặc định
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
    }
    
    private void initializeRoles() {
        // Tạo vai trò KHÁCH HÀNG nếu chưa tồn tại
        if (!roleRepository.existsByName("CUSTOMER")) {
            Role customerRole = new Role();
            customerRole.setName("CUSTOMER");
            customerRole.setDescription("Vai trò khách hàng cho người dùng thông thường");
            customerRole.setCreatedAt(java.time.LocalDateTime.now());
            customerRole.setUpdatedAt(java.time.LocalDateTime.now());
            roleRepository.save(customerRole);
            System.out.println("Đã tạo vai trò KHÁCH HÀNG");
        }
        
        // Tạo vai trò QUẢN TRỊ VIÊN nếu chưa tồn tại
        if (!roleRepository.existsByName("ADMIN")) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Vai trò quản trị viên cho người quản lý hệ thống");
            adminRole.setCreatedAt(java.time.LocalDateTime.now());
            adminRole.setUpdatedAt(java.time.LocalDateTime.now());
            roleRepository.save(adminRole);
            System.out.println("Đã tạo vai trò QUẢN TRỊ VIÊN");
        }
        
        // Tạo vai trò NGƯỜI BÁN nếu chưa tồn tại
        if (!roleRepository.existsByName("SELLER")) {
            Role sellerRole = new Role();
            sellerRole.setName("SELLER");
            sellerRole.setDescription("Vai trò người bán cho các thương gia");
            sellerRole.setCreatedAt(java.time.LocalDateTime.now());
            sellerRole.setUpdatedAt(java.time.LocalDateTime.now());
            roleRepository.save(sellerRole);
            System.out.println("Đã tạo vai trò NGƯỜI BÁN");
        }
        
        // Tạo vai trò KIỂM DUYỆT VIÊN nếu chưa tồn tại
        if (!roleRepository.existsByName("MODERATOR")) {
            Role moderatorRole = new Role();
            moderatorRole.setName("MODERATOR");
            moderatorRole.setDescription("Vai trò kiểm duyệt viên cho người kiểm duyệt nội dung");
            moderatorRole.setCreatedAt(java.time.LocalDateTime.now());
            moderatorRole.setUpdatedAt(java.time.LocalDateTime.now());
            roleRepository.save(moderatorRole);
            System.out.println("Đã tạo vai trò KIỂM DUYỆT VIÊN");
        }
    }
}
