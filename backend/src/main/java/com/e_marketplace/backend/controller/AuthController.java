package com.e_marketplace.backend.controller;

import com.e_marketplace.backend.dto.AuthResponse;
import com.e_marketplace.backend.dto.LoginRequest;
import com.e_marketplace.backend.dto.RegisterRequest;
import com.e_marketplace.backend.entity.User;
import com.e_marketplace.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller cho các endpoint xác thực
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * Đăng ký người dùng mới
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponse response = authService.register(registerRequest);
            
            if (response.getMessage() != null && response.getMessage().equals("Email đã tồn tại")) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Email đã tồn tại");
                error.put("field", "email");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Đăng ký thất bại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Đăng nhập người dùng
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.login(loginRequest);
            
            if (response.getMessage() != null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", response.getMessage());
                
                if (response.getMessage().contains("Email hoặc mật khẩu không đúng")) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
                } else if (response.getMessage().contains("Tài khoản đã bị vô hiệu hóa")) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
                }
                
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Đăng nhập thất bại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Xác thực token
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Header xác thực không hợp lệ");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            String token = authHeader.substring(7); // Loại bỏ tiền tố "Bearer "
            boolean isValid = authService.validateToken(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            
            if (isValid) {
                response.put("message", "Token hợp lệ");
            } else {
                response.put("message", "Token không hợp lệ hoặc đã hết hạn");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Xác thực token thất bại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Lấy thông tin hồ sơ người dùng
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Header xác thực không hợp lệ");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            String token = authHeader.substring(7); // Loại bỏ tiền tố "Bearer "
            
            if (!authService.validateToken(token)) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Token không hợp lệ hoặc đã hết hạn");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            return authService.getUserByToken(token)
                    .map(user -> {
                        Map<String, Object> profile = new HashMap<>();
                        profile.put("id", user.getId());
                        profile.put("email", user.getEmail());
                        profile.put("firstName", user.getFirstName());
                        profile.put("lastName", user.getLastName());
                        profile.put("phoneNumber", user.getPhone());
                        profile.put("isActive", user.getStatus() == User.UserStatus.ACTIVE);
                        profile.put("createdAt", user.getCreatedAt());
                        profile.put("lastLoginAt", user.getLastLoginAt());
                        return ResponseEntity.ok(profile);
                    })
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("message", "Không tìm thấy người dùng")));
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Lấy thông tin hồ sơ thất bại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Endpoint kiểm tra sức khỏe
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Dịch vụ xác thực");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}
