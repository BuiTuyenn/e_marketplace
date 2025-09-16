package com.e_marketplace.backend.service;

import com.e_marketplace.backend.dto.AuthResponse;
import com.e_marketplace.backend.dto.LoginRequest;
import com.e_marketplace.backend.dto.RegisterRequest;
import com.e_marketplace.backend.entity.Role;
import com.e_marketplace.backend.entity.User;
import com.e_marketplace.backend.entity.UserRole;
import com.e_marketplace.backend.repository.RoleRepository;
import com.e_marketplace.backend.repository.UserRepository;
import com.e_marketplace.backend.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service xử lý các thao tác xác thực
 */
@Service
@Transactional
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Đăng ký người dùng mới
     */
    public AuthResponse register(RegisterRequest registerRequest) {
        try {
            // Kiểm tra xem người dùng đã tồn tại chưa
            if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                return new AuthResponse("Email đã tồn tại");
            }
            
            // Tạo người dùng mới
            User user = new User();
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setEmail(registerRequest.getEmail());
            user.setPhone(registerRequest.getPhoneNumber());
            user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
            user.setStatus(User.UserStatus.ACTIVE);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            
            // Lưu người dùng
            User savedUser = userRepository.save(user);
            
            // Gán vai trò mặc định (KHÁCH HÀNG)
            Role customerRole = roleRepository.findByName("CUSTOMER")
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò khách hàng"));
            
            UserRole userRole = new UserRole();
            userRole.setUser(savedUser);
            userRole.setRole(customerRole);
            userRole.setCreatedAt(LocalDateTime.now());
            userRoleRepository.save(userRole);
            
            // Tạo token (hiện tại trả về token đơn giản)
            String token = generateSimpleToken(savedUser.getId());
            
            return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getPhone(),
                customerRole.getName(),
                LocalDateTime.now().plusHours(24) // Token hết hạn sau 24 giờ
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Đăng ký thất bại: " + e.getMessage());
        }
    }
    
    /**
     * Đăng nhập người dùng
     */
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            // Tìm người dùng theo email
            Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
            if (userOptional.isEmpty()) {
                return new AuthResponse("Email hoặc mật khẩu không đúng");
            }
            
            User user = userOptional.get();
            
            // Kiểm tra tài khoản có hoạt động không
            if (user.getStatus() != User.UserStatus.ACTIVE) {
                return new AuthResponse("Tài khoản đã bị vô hiệu hóa");
            }
            
            // Xác minh mật khẩu
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                return new AuthResponse("Email hoặc mật khẩu không đúng");
            }
            
            // Lấy vai trò người dùng
            Optional<UserRole> userRoleOptional = userRoleRepository.findByUser(user);
            String roleName = "CUSTOMER"; // Vai trò mặc định
            if (userRoleOptional.isPresent()) {
                roleName = userRoleOptional.get().getRole().getName();
            }
            
            // Cập nhật lần đăng nhập cuối
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            
            // Tạo token
            String token = generateSimpleToken(user.getId());
            
            return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                roleName,
                LocalDateTime.now().plusHours(24) // Token hết hạn sau 24 giờ
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Đăng nhập thất bại: " + e.getMessage());
        }
    }
    
    /**
     * Tạo token đơn giản (trong sản xuất, sử dụng JWT)
     */
    private String generateSimpleToken(Long userId) {
        // Đây là cách tạo token đơn giản cho mục đích demo
        // Trong sản xuất, sử dụng JWT hoặc cách tạo token bảo mật tương tự
        return "token_" + userId + "_" + System.currentTimeMillis();
    }
    
    /**
     * Xác thực token (xác thực đơn giản cho demo)
     */
    public boolean validateToken(String token) {
        try {
            if (token == null || !token.startsWith("token_")) {
                return false;
            }
            
            String[] parts = token.split("_");
            if (parts.length != 3) {
                return false;
            }
            
            Long userId = Long.parseLong(parts[1]);
            return userRepository.existsById(userId);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Lấy người dùng theo token
     */
    public Optional<User> getUserByToken(String token) {
        try {
            if (token == null || !token.startsWith("token_")) {
                return Optional.empty();
            }
            
            String[] parts = token.split("_");
            if (parts.length != 3) {
                return Optional.empty();
            }
            
            Long userId = Long.parseLong(parts[1]);
            return userRepository.findById(userId);
            
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
